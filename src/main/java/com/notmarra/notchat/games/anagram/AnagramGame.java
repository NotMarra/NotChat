package com.notmarra.notchat.games.anagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notchat.utils.ChatFormatter;
import com.notmarra.notchat.utils.MinecraftStuff;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class AnagramGame extends ChatGame {
    public static String GAME_ID = "anagram";

    private static final int MAX_INCORRECT_GUESSES = 6;
    
    private String originalWord;
    private String scrambledWord;
    private int attempts;
    private int hintsUsed;
    private boolean solved;

    private boolean minecraftBlocks;
    private boolean minecraftItems;
    private boolean minecraftMobs;
    private boolean minecraftBiomes;
    private List<String> customWords;
    
    public AnagramGame(JavaPlugin plugin, String id) {
        super(plugin, id);
        
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
            allWords.addAll(MinecraftStuff.getInstance().blockIdNames);
        }
        
        if (minecraftItems) {
            allWords.addAll(MinecraftStuff.getInstance().itemIdNames);
        }
        
        if (minecraftMobs) {
            allWords.addAll(MinecraftStuff.getInstance().entityIdNames);
        }
        
        if (minecraftBiomes) {
            allWords.addAll(MinecraftStuff.getInstance().biomeIdNames);
        }
        
        allWords.addAll(customWords);
        
        // if no categories, use a default set
        if (allWords.isEmpty()) {
            allWords.addAll(MinecraftStuff.getInstance().blockIdNames);
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
    public void onHint(Player player) {
        hintsUsed++;

        if (hintsUsed == 1) {
            player.sendMessage("První písmeno je: " + originalWord.charAt(0));
        } else if (hintsUsed == 2) {
            if (originalWord.contains(" ")) {
                int spaceIndex = originalWord.indexOf(' ');
                player.sendMessage("První písmeno druhého slova je: " + originalWord.charAt(spaceIndex + 1));
            } else {
                player.sendMessage("Druhé písmeno je: " + originalWord.charAt(1));
            }
        } else if (hintsUsed == 3) {
            if (MinecraftStuff.getInstance().blockIdNames.contains(originalWord)) {
                player.sendMessage("Je to blok!");
            } else if (MinecraftStuff.getInstance().itemIdNames.contains(originalWord)) {
                player.sendMessage("Je to item!");
            } else if (MinecraftStuff.getInstance().entityIdNames.contains(originalWord)) {
                player.sendMessage("Je to mob!");
            } else if (MinecraftStuff.getInstance().biomeIdNames.contains(originalWord)) {
                player.sendMessage("Je to biome!");
            } else {
                player.sendMessage("Nemohu detekovat typ slova.");
            }
        } else {
            player.sendMessage("Slovo je: " + originalWord);
        }
    }

    @Override
    public String getGameSummary() {
        if (solved) {
            return "Správně jsi uhádl slovo '" + originalWord + "'!";
        } else {
            return "Nepodařilo se ti uhádnout slovo '" + originalWord + "' s " + MAX_INCORRECT_GUESSES + " pokusy.";
        }
    }
    
    @Override
    public void start(Player player) {
        player.sendMessage(Component.text("[[[ ANAGRAM ]]]", TextColor.color(30, 144, 255)));
        player.sendMessage("Najdi správné slovo: " + scrambledWord);

        ChatFormatter hintMessage = ChatFormatter.empty();
        hintMessage.append(Component.text("/answer <slovo>", Style.style(TextDecoration.BOLD)));
        hintMessage.append(Component.text(" nebo "));
        hintMessage.append(Component.text("/hint", Style.style(TextDecoration.BOLD)));
        hintMessage.append(Component.text(" pro nápovědu."));
        player.sendMessage(hintMessage.build());
    }
    
    @Override
    public ChatGameResponse onAnswer(Player player, String answer) {
        answer = answer.trim().toUpperCase();

        attempts++;

        if (answer.equalsIgnoreCase(originalWord)) {
            solved = true;
            return ChatGameResponse.endGameCorrect();
        }

        if (attempts >= MAX_INCORRECT_GUESSES) {
            return ChatGameResponse.endGameIncorrect();
        }

        return ChatGameResponse.incorrect();
    }
}
