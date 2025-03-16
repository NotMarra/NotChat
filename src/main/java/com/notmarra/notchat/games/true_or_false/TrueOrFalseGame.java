package com.notmarra.notchat.games.true_or_false;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notlib.utils.ChatF;

public class TrueOrFalseGame extends ChatGame {
    public static String GAME_ID = "true_or_false";

    private String questionText;
    private boolean correctAnswer;
    private boolean correctlyAnswered;
    private boolean completed;

    private final Random random = new Random();

    private boolean minecraftBlocks;
    private boolean minecraftItems;
    private boolean minecraftMobs;
    private boolean minecraftBiomes;
    private List<CustomQuestion> customQuestions;

    public static class QuestionAnswer {
        private String question;
        private boolean answer;

        public QuestionAnswer(String question, boolean answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public boolean getAnswer() {
            return answer;
        }
    }

    public static class CustomQuestion {
        private String question;
        private boolean answer;

        public CustomQuestion(String question, boolean answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public boolean getAnswer() {
            return answer;
        }
    }
    
    public TrueOrFalseGame(JavaPlugin plugin, String id) {
        super(plugin, id);
        
        this.completed = false;
        
        minecraftBlocks = settings.getBoolean("minecraft_blocks", true);
        minecraftItems = settings.getBoolean("minecraft_items", true);
        minecraftMobs = settings.getBoolean("minecraft_mobs", true);
        minecraftBiomes = settings.getBoolean("minecraft_biomes", true);
        customQuestions = loadCustomQuestions();

        generateQuestion();
    }

    public boolean isGameOver() {
        return completed;
    }

    private List<CustomQuestion> loadCustomQuestions() {
        ConfigurationSection customQuestionsSection = settings.getConfigurationSection("custom_questions");

        if (customQuestionsSection == null) {
            return new ArrayList<>();
        }

        List<CustomQuestion> questions = new ArrayList<>();
        Set<String> keys = customQuestionsSection.getKeys(false);
        
        for (String key : keys) {
            ConfigurationSection questionSection = customQuestionsSection.getConfigurationSection(key);
            if (questionSection == null) continue;
            if (!questionSection.contains("question") || !questionSection.contains("answer")) continue;
            if (!questionSection.isString("question") || !questionSection.isBoolean("answer")) continue;
            questions.add(new CustomQuestion(
                questionSection.getString("question"),
                questionSection.getBoolean("answer")
            ));
        }
        
        return questions;
    }

    private void generateQuestion() {
        // 20% chance to use a custom question
        if (!customQuestions.isEmpty() && random.nextInt(5) == 0) {
            CustomQuestion selected = customQuestions.get(random.nextInt(customQuestions.size()));
            
            questionText = selected.getQuestion();
            correctAnswer = selected.getAnswer();
        } else {
            List<String> enabledCategories = new ArrayList<>();
            
            if (minecraftBlocks) enabledCategories.add("block");
            if (minecraftItems) enabledCategories.add("item");
            if (minecraftMobs) enabledCategories.add("mob");
            if (minecraftBiomes) enabledCategories.add("biome");
            if (enabledCategories.isEmpty()) enabledCategories.add("block");
            
            String category = enabledCategories.get(random.nextInt(enabledCategories.size()));
            
            boolean isTrue = random.nextBoolean();
            
            QuestionAnswer qa = generateQuestionForCategory(category, isTrue);
            questionText = qa.getQuestion();
            correctAnswer = qa.getAnswer();
        }
    }

    private QuestionAnswer generateQuestionForCategory(String category, boolean shouldBeTrue) {
        String subject = "";
        String predicate = "";

        String name;
        String type;
        Map<String, String> properties;
        
        // Select object and property based on category
        switch (category) {
            default:
            case "block":
                name = TrueOrFalseKnowledge.BLOCKS.get(random.nextInt(TrueOrFalseKnowledge.BLOCKS.size()));
                subject = "a " + name.replace('_', ' ');
                type = "block";
                properties = TrueOrFalseKnowledge.BLOCK_PROPERTIES;
                break;
        
            case "item":
                name = TrueOrFalseKnowledge.ITEMS.get(random.nextInt(TrueOrFalseKnowledge.ITEMS.size()));
                subject = "a " + name.replace('_', ' ');
                type = "item";
                properties = TrueOrFalseKnowledge.ITEM_PROPERTIES;
                break;
    
            case "mob":
                name = TrueOrFalseKnowledge.MOBS.get(random.nextInt(TrueOrFalseKnowledge.MOBS.size()));
                subject = "a " + name.replace('_', ' ');
                type = "mob";
                properties = TrueOrFalseKnowledge.MOB_PROPERTIES;
                break;

            case "biome":
                name = TrueOrFalseKnowledge.BIOMES.get(random.nextInt(TrueOrFalseKnowledge.BIOMES.size()));
                subject = "the " + name.replace('_', ' ') + " biome";
                type = "biome";
                properties = TrueOrFalseKnowledge.BIOME_PROPERTIES;
                break;
        }

        if (shouldBeTrue) {
            predicate = properties.get(name);
        } else {
            String trueProp = properties.get(name);
            List<String> falseProps = new ArrayList<>(TrueOrFalseKnowledge.FALSE_PROPERTIES.get(type));
            falseProps.removeIf(prop -> trueProp.toLowerCase().contains(prop.toLowerCase()));
            predicate = falseProps.get(random.nextInt(falseProps.size()));
        }
        
        // Format the question using templates
        List<String> templates = shouldBeTrue ? TrueOrFalseKnowledge.TRUE_TEMPLATES : TrueOrFalseKnowledge.FALSE_TEMPLATES;
        
        String template = templates.get(random.nextInt(templates.size()));
        String question = String.format(template, subject, predicate);
        
        return new QuestionAnswer(question, shouldBeTrue);
    }

    @Override
    public ChatF getTitle() {
        return ChatF.ofBold("[[[ TRUE OR FALSE ]]]", ChatF.C_DODGERBLUE);
    }

    @Override
    public ChatF onHint(Player player) {
        return ChatF.of("Žádná nápověda pro tuto hru.");
    }

    @Override
    public ChatF getGameSummary() {
        if (completed) {
            return ChatF.of("You " + (correctlyAnswered ? "correctly" : "incorrectly") + " answered the question: '" + questionText + "'");
        } else {
            // NOTE: should never happen in normal game flow
            return ChatF.of("You did not complete the true/false question.");
        }
    }
    
    @Override
    public void start(Player player) {
        ChatF message1 = ChatF.empty()
            .append("Is the following statement true or false?");
        player.sendMessage(message1.build());

        ChatF message2 = ChatF.empty()
            .appendBold(questionText);
        player.sendMessage(message2.build());

        ChatF message = ChatF.empty()
            .append("Type ")
            .appendBold("true", ChatF.C_GREEN)
            .append(" or ")
            .appendBold("false", ChatF.C_RED)
            .append(" to answer.");
        player.sendMessage(message.build());
    }
    
    @Override
    public ChatGameResponse onAnswer(Player player, String answer) {
        answer = answer.trim().toUpperCase();

        if (!answer.equals("TRUE") && !answer.equals("FALSE")) {
            ChatF message = ChatF.empty()
                .append("Type ")
                .appendBold("true", ChatF.C_GREEN)
                .append(" or ")
                .appendBold("false", ChatF.C_RED)
                .append(" to answer.");
            player.sendMessage(message.build());
            return ChatGameResponse.incorrect();
        }

        boolean playerAnswer = answer.equals("TRUE");
        completed = true;
        correctlyAnswered = playerAnswer == correctAnswer;

        if (correctlyAnswered) {
            return ChatGameResponse.endGameCorrect();
        }

        return ChatGameResponse.endGameIncorrect();
    }
}
