package com.notmarra.notchat.games.fast_type;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notchat.utils.MinecraftStuff;

public class FastTypeGame extends ChatGame {
    public static String GAME_ID = "fast_type";

    private String targetWord;
    private static final int MAX_TIME = 30;
    private boolean completed;
    private long completionTime;

    private boolean minecraftBlocks;
    private boolean minecraftItems;
    private boolean minecraftMobs;
    private boolean minecraftBiomes;
    private List<String> customWords;
    
    public FastTypeGame(JavaPlugin plugin, String id) {
        super(plugin, id);
        
        this.completed = false;
        
        minecraftBlocks = settings.getBoolean("minecraft_blocks", true);
        minecraftItems = settings.getBoolean("minecraft_items", true);
        minecraftMobs = settings.getBoolean("minecraft_mobs", true);
        minecraftBiomes = settings.getBoolean("minecraft_biomes", true);
        customWords = settings.getStringList("custom_words");

        this.targetWord = selectRandomWord();
    }

    public boolean isGameOver() {
        if (completed) {
            return true;
        }

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        return elapsedTime > MAX_TIME * 1000;
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

    private String formatTime(long timeInMillis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % 60;
        long millis = timeInMillis % 1000;
        return String.format("%02d:%03d", seconds, millis);
    }

    private double calculateWPM(String word, long timeInMillis) {
        double minutes = timeInMillis / 60000.0;
        double charCount = word.length();
        double standardizedWords = charCount / 5.0;
        return standardizedWords / minutes;
    }

    @Override
    public ChatF getTitle() {
        return ChatF.ofBold("[[[ FAST TYPE ]]]", ChatF.C_DODGERBLUE);
    }

    @Override
    public ChatF onHint(Player player) {
        return ChatF.of("Žádná nápověda pro tuto hru.");
    }

    @Override
    public ChatF getGameSummary() {
        if (completed) {
            double wpm = calculateWPM(targetWord, completionTime);
            return ChatF.of("Správně jsi napsal slovo '" + targetWord + "' za " + formatTime(completionTime) + " s rychlostí WPM " + String.format("%.2f", wpm) + "!");
        } else {
            return ChatF.of("Nepodařilo se ti napsat slovo '" + targetWord + "' v časovém limitu " + MAX_TIME + "s.");
        }
    }
    
    @Override
    public void start(Player player) {
        ChatF.empty()
            .append("Napiš slovo: ")
            .appendBold(targetWord)
            .sendTo(player);

        ChatF.empty()
            .append("Máš ")
            .appendBold(MAX_TIME + " sekund", ChatF.C_RED)
            .append("!")
            .sendTo(player);

        ChatF.empty()
            .appendBold("/answer <slovo>")
            .append(" pro odeslání odpovědi.")
            .sendTo(player);
    }
    
    @Override
    public ChatGameResponse onAnswer(Player player, String answer) {
        answer = answer.trim().toUpperCase();

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        if (elapsedTime > MAX_TIME * 1000) {
            return ChatGameResponse.endIncorrect();
        }

        if (answer.equalsIgnoreCase(targetWord)) {
            completionTime = elapsedTime;
            completed = true;
            return ChatGameResponse.endCorrect();
        }

        return ChatGameResponse.endIncorrect();
    }
}
