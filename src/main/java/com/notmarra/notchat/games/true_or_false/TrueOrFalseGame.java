package com.notmarra.notchat.games.true_or_false;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notlib.utils.ChatF;

public class TrueOrFalseGame extends ChatGame {
    public static String GAME_ID = "true_or_false";

    private Question currentQuestion;
    private boolean correctlyAnswered;
    private boolean completed;

    private final Random random = new Random();
    private final QuestionGenerator questionGenerator;

    private boolean minecraftBlocks;
    private boolean minecraftItems;
    private boolean minecraftMobs;
    private boolean minecraftBiomes;
    private List<Question> customQuestions;
    private int customQuestionProbability; // percentage (0-100)
    private boolean showCorrectAnswerOnFail;

    public static class Question {
        private final String text;
        private final boolean answer;
        private final String category;
        private @Nullable String hint;
        private @Nullable String explanation;
        private List<String> rewardCommands;

        public Question(String text, boolean answer, String category) {
            this.text = text;
            this.answer = answer;
            this.category = category;
            this.hint = null;
            this.explanation = null;
            this.rewardCommands = new ArrayList<>();
        }

        public String getText() {
            return text;
        }

        public boolean getAnswer() {
            return answer;
        }

        public String getCategory() {
            return category;
        }

        public boolean hasHint() {
            return hint != null;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public boolean hasExplanation() {
            return explanation != null;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        public List<String> getRewardCommands() {
            return rewardCommands;
        }

        public void setRewardCommands(List<String> rewardCommands) {
            this.rewardCommands = rewardCommands;
        }

        public boolean hasRewardCommands() {
            return !rewardCommands.isEmpty();
        }
    }
    
    public TrueOrFalseGame(JavaPlugin plugin, String id) {
        super(plugin, id);
        
        this.completed = false;
        
        minecraftBlocks = settings.getBoolean("minecraft_blocks", true);
        minecraftItems = settings.getBoolean("minecraft_items", true);
        minecraftMobs = settings.getBoolean("minecraft_mobs", true);
        minecraftBiomes = settings.getBoolean("minecraft_biomes", true);
        customQuestionProbability = settings.getInt("custom_question_probability", 0);
        showCorrectAnswerOnFail = settings.getBoolean("show_correct_answer_on_fail", true);

        customQuestions = loadCustomQuestions();
        questionGenerator = new QuestionGenerator(TrueOrFalseKnowledge.getInstance());

        generateQuestion();
    }

    public boolean isGameOver() {
        return completed;
    }

    private List<Question> loadCustomQuestions() {
        ConfigurationSection customQuestionsSection = settings.getConfigurationSection("custom_questions");

        if (customQuestionsSection == null) {
            return new ArrayList<>();
        }

        List<Question> questions = new ArrayList<>();
        Set<String> keys = customQuestionsSection.getKeys(false);
        
        for (String key : keys) {
            ConfigurationSection questionSection = customQuestionsSection.getConfigurationSection(key);

            if (questionSection == null) continue;
            if (!questionSection.isString("question") || !questionSection.isBoolean("answer")) continue;

            String category = questionSection.getString("category", "custom");

            Question question = new Question(
                questionSection.getString("question"),
                questionSection.getBoolean("answer"),
                category
            );

            if (questionSection.isString("hint")) {
                question.setHint(questionSection.getString("hint"));
            }
            if (questionSection.isString("explanation")) {
                question.setExplanation(questionSection.getString("explanation"));
            }
            if (questionSection.isList("reward_commands")) {
                question.setRewardCommands(questionSection.getStringList("reward_commands"));
            }
            
            questions.add(question);
        }
        
        return questions;
    }

    private void generateQuestion() {
        if (!customQuestions.isEmpty() && random.nextInt(100) < customQuestionProbability) {
            currentQuestion = customQuestions.get(random.nextInt(customQuestions.size()));
            return;
        }

        List<String> categories = new ArrayList<>();
        
        if (minecraftBlocks) categories.add("block");
        if (minecraftItems) categories.add("item");
        if (minecraftMobs) categories.add("mob");
        if (minecraftBiomes) categories.add("biome");
        if (categories.isEmpty()) categories.add("block");
        
        String category = categories.get(random.nextInt(categories.size()));
        currentQuestion = questionGenerator.generateQuestion(category);
    }

    private void printQuestionHelp(Player player) {
        ChatF.empty()
            .append("Napiš ")
            .appendBold("/answer ", ChatF.C_LIGHTGRAY)
            .appendBold("true", ChatF.C_GREEN)
            .append(" nebo ")
            .appendBold("/answer ", ChatF.C_LIGHTGRAY)
            .appendBold("false", ChatF.C_RED)
            .append(" pro odpověď.")
            .sendTo(player);

        if (currentQuestion.hasHint()) {
            ChatF.empty()
                .append("Napiš ")
                .appendBold("/hint", ChatF.C_YELLOW)
                .append(" pro nápovědu.")
                .sendTo(player);
        }
    }

    @Override
    public ChatF getTitle() {
        return ChatF.ofBold("[[[ TRUE OR FALSE ]]]", ChatF.C_DODGERBLUE);
    }

    @Override
    public ChatF onHint(Player player) {
        if (currentQuestion.hasHint()) {
            return ChatF.of("Hint: " + currentQuestion.getHint(), ChatF.C_YELLOW);
        } else {
            return ChatF.of("Žádná nápověda pro tuto otázku.");
        }
    }

    @Override
    public ChatF getGameSummary() {
        if (completed) {
            return ChatF.empty()
                .appendBold(
                    correctlyAnswered ? "Správně" : "Nesprávně",
                    correctlyAnswered ? ChatF.C_GREEN : ChatF.C_RED
                )
                .append(" si odpověděl na otázku!");
        } else {
            // NOTE: should never happen in normal game flow
            return ChatF.of("Hra není dokončena.");
        }
    }
    
    @Override
    public void start(Player player) {
        ChatF.empty()
            .append("Je toto tvrzení pravdivé?")
            .sendTo(player);

        ChatF.empty()
            .appendBold(currentQuestion.getText(), ChatF.C_YELLOW)
            .sendTo(player);

        printQuestionHelp(player);
    }
    
    @Override
    public ChatGameResponse onAnswer(Player player, String answer) {
        answer = answer.trim().toUpperCase();

        if (!answer.equals("TRUE") && !answer.equals("FALSE")) {
            printQuestionHelp(player);
            return ChatGameResponse.incorrect();
        }

        boolean playerAnswer = answer.equals("TRUE");
        completed = true;
        correctlyAnswered = playerAnswer == currentQuestion.getAnswer();

        if (!correctlyAnswered && showCorrectAnswerOnFail) {
            ChatF.empty()
                .append("Správná odpověď: ")
                .appendBold(
                    currentQuestion.getAnswer() ? "TRUE" : "FALSE",
                    currentQuestion.getAnswer() ? ChatF.C_GREEN : ChatF.C_RED
                )
                .sendTo(player);

            if (currentQuestion.hasExplanation()) {
                ChatF.empty()
                    .append("Vysvětlení: ")
                    .append(currentQuestion.getExplanation(), ChatF.C_GRAY)
                    .sendTo(player);
            }
        }

        if (correctlyAnswered) {
            return ChatGameResponse.endCorrect().withRewardCommands(
                currentQuestion.getRewardCommands()
            );
        }

        return ChatGameResponse.endIncorrect();
    }
}
