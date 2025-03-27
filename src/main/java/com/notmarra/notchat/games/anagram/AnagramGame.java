package com.notmarra.notchat.games.anagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notlib.extensions.NotMinecraftStuff;
import com.notmarra.notlib.utils.ChatF;

public class AnagramGame extends ChatGame {
    public static String ID = "anagram";

    private String originalWord;
    private String scrambledWord;
    private int hintsUsed;
    private boolean solved;

    private boolean minecraftBlocks;
    private boolean minecraftItems;
    private boolean minecraftMobs;
    private boolean minecraftBiomes;
    private List<String> customWords;
    
    public AnagramGame(NotChat plugin) {
        super(plugin);
        
        this.hintsUsed = 0;
        this.solved = false;
        
        minecraftBlocks = settings.getBoolean("minecraft_blocks", true);
        minecraftItems = settings.getBoolean("minecraft_items", true);
        minecraftMobs = settings.getBoolean("minecraft_mobs", true);
        minecraftBiomes = settings.getBoolean("minecraft_biomes", true);
        customWords = settings.getStringList("custom_words");

        this.originalWord = selectRandomWord();
        this.scrambledWord = scrambleWord(originalWord);
    }
    
    private String selectRandomWord() {
        List<String> allWords = new ArrayList<>();
        
        // Add words from enabled categories
        if (minecraftBlocks) {
            allWords.addAll(NotMinecraftStuff.getInstance().blockIdNames);
        }
        
        if (minecraftItems) {
            allWords.addAll(NotMinecraftStuff.getInstance().itemIdNames);
        }
        
        if (minecraftMobs) {
            allWords.addAll(NotMinecraftStuff.getInstance().entityIdNames);
        }
        
        if (minecraftBiomes) {
            allWords.addAll(NotMinecraftStuff.getInstance().biomeIdNames);
        }
        
        allWords.addAll(customWords);
        
        // if no categories, use a default set
        if (allWords.isEmpty()) {
            allWords.addAll(NotMinecraftStuff.getInstance().blockIdNames);
        }
        
        return allWords.get((new Random()).nextInt(allWords.size())).toUpperCase();
    }

    // preserves spaces, but scrambles the letters across whole word/phrase
    private String scrambleWord(String word) {
        List<Integer> spacePositions = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == ' ') {
                spacePositions.add(i);
            }
        }
        
        String lettersOnly = word.replace(" ", "");
        char[] letters = lettersOnly.toCharArray();
        
        Random random = new Random();
        for (int i = letters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = letters[i];
            letters[i] = letters[j];
            letters[j] = temp;
        }
        
        StringBuilder scrambled = new StringBuilder();
        int letterIndex = 0;
        for (int i = 0; i < word.length(); i++) {
            if (spacePositions.contains(i)) {
                scrambled.append(' ');
            } else {
                scrambled.append(letters[letterIndex++]);
            }
        }
        
        String result = scrambled.toString();
        
        if (result.equals(word)) {
            return scrambleWord(word);
        }
        
        return result;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public ChatF getTitle() {
        return ChatF.ofBold("[[[ ANAGRAM ]]]", ChatF.C_DODGERBLUE);
    }

    @Override
    public ChatF onHint(Player player) {
        hintsUsed++;

        if (hintsUsed < 4) {
            ChatF hint = ChatF.empty()
                .appendBold("[HINT " + hintsUsed + "]", ChatF.C_GOLD)
                .append(" ");

            if (hintsUsed == 1) {
                hint.append("Počáteční písmeno je: ");
                hint.appendBold(String.valueOf(originalWord.charAt(0)), ChatF.C_GOLD);
            }

            if (hintsUsed == 2) {
                if (originalWord.contains(" ")) {
                    int spaceIndex = originalWord.indexOf(' ');
                    hint.append("První písmeno druhého slova je: ");
                    hint.appendBold(String.valueOf(originalWord.charAt(spaceIndex + 1)), ChatF.C_GOLD);
                } else {
                    hint.append("Druhé písmeno je: ");
                    hint.appendBold(String.valueOf(originalWord.charAt(1)), ChatF.C_GOLD);
                }
            }

            if (hintsUsed == 3) {
                hint.append("Typ slova: ");
    
                if (NotMinecraftStuff.getInstance().blockIdNames.contains(originalWord)) {
                    hint.appendBold("BLOK", ChatF.C_GOLD);
                } else if (NotMinecraftStuff.getInstance().itemIdNames.contains(originalWord)) {
                    hint.appendBold("ITEM", ChatF.C_GOLD);
                } else if (NotMinecraftStuff.getInstance().entityIdNames.contains(originalWord)) {
                    hint.appendBold("MOB", ChatF.C_GOLD);
                } else if (NotMinecraftStuff.getInstance().biomeIdNames.contains(originalWord)) {
                    hint.appendBold("BIOME", ChatF.C_GOLD);
                } else {
                    hint.appendBold("NEMOHU DETEKOVAT", ChatF.C_RED);
                }
            }

            return hint;
        }

        return ChatF.ofBold("Nemám žádné další nápovědy.", ChatF.C_RED);
    }

    @Override
    public ChatF getGameSummary() {
        if (solved) {
            return ChatF.of("Správně jsi uhádl slovo '" + originalWord + "'!");
        } else {
            return ChatF.of("Nepodařilo se ti uhádnout slovo '" + originalWord + "'!");
        }
    }
    
    @Override
    public void start(Player player) {
        ChatF.empty()
            .append("Přeuspořádej slovo: ")
            .appendBold(scrambledWord, ChatF.C_GOLD)
            .sendTo(player);

        ChatF.empty()
            .appendBold("/answer <slovo>", ChatF.C_LIGHTGRAY)
            .append(" nebo ")
            .appendBold("/hint", ChatF.C_YELLOW)
            .append(" pro nápovědu.")
            .sendTo(player);
    }
    
    @Override
    public ChatGameResponse onAnswer(Player player, String answer) {
        answer = answer.trim().toUpperCase();

        if (answer.equalsIgnoreCase(originalWord)) {
            solved = true;
            return ChatGameResponse.endCorrect();
        }

        return ChatGameResponse.endIncorrect();
    }
}
