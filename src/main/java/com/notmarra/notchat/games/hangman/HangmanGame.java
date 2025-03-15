package com.notmarra.notchat.games.hangman;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notchat.utils.ChatFormatter;
import com.notmarra.notchat.utils.MinecraftStuff;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class HangmanGame extends ChatGame {
    public static String GAME_ID = "hangman";

    private static final int MAX_INCORRECT_GUESSES = 6;
    
    private String word;
    private Set<Character> guessedLetters;
    private int incorrectGuesses;

    private boolean minecraftBlocks;
    private boolean minecraftItems;
    private boolean minecraftMobs;
    private boolean minecraftBiomes;
    private List<String> customWords;
    
    public HangmanGame(JavaPlugin plugin, String id) {
        super(plugin, id);
        
        this.guessedLetters = new HashSet<>();
        this.incorrectGuesses = 0;
        
        minecraftBlocks = settings.getBoolean("minecraft_blocks", true);
        minecraftItems = settings.getBoolean("minecraft_items", true);
        minecraftMobs = settings.getBoolean("minecraft_mobs", true);
        minecraftBiomes = settings.getBoolean("minecraft_biomes", true);
        customWords = settings.getStringList("custom_words");

        this.word = selectRandomWord();
    }

    public boolean isGameOver() {
        return isWordGuessed() || incorrectGuesses >= MAX_INCORRECT_GUESSES;
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
    
    private String getCurrentWordState() {
        StringBuilder display = new StringBuilder();
        
        for (char c : word.toCharArray()) {
            if (guessedLetters.contains(Character.toLowerCase(c))) {
                display.append(c);
            } else {
                if (c == ' ') {
                    display.append(" ");
                } else {
                    display.append("_");
                }
            }
            display.append(" ");
        }
        
        return display.toString();
    }
    
    private List<CharSequence> getIncorrectGuesses() {
        List<CharSequence> incorrect = new ArrayList<>();
        
        for (char c : guessedLetters) {
            if (word.indexOf(c) == -1) {
                incorrect.add(Character.toString(c));
            }
        }
        
        return incorrect;
    }
    
    private boolean isWordGuessed() {
        for (char c : word.toCharArray()) {
            if (c != ' ' && !guessedLetters.contains(Character.toLowerCase(c))) {
                return false;
            }
        }
        return true;
    }
    
    private Component getHangmanState() {
        TextColor red = TextColor.color(255, 0, 0);
        ChatFormatter nl = ChatFormatter.of("\n");

        ChatFormatter piece1 = ChatFormatter.of("--#####")
            .append(" ")
            .append("Uhádni slovo a nenech se oběsit!");

        ChatFormatter piece2 = ChatFormatter.of("--#---#");

        ChatFormatter piece3 = ChatFormatter.empty();
        if (incorrectGuesses >= 1) {
            piece3.append("--");
            piece3.append(Component.text("O", red));
            piece3.append("---# ");
            piece3.append(getCurrentWordState());
        } else {
            piece3.append("------# ");
            piece3.append(getCurrentWordState());
        }

        ChatFormatter piece4 = ChatFormatter.empty();
        if (incorrectGuesses >= 2) {
            piece4.append("-");
            piece4.append(Component.text("/", red));
            if (incorrectGuesses >= 4) {
                piece4.append(Component.text("O\\", red));
                piece4.append("--#");
            } else if (incorrectGuesses >= 3) {
                piece4.append(Component.text("O", red));
                piece4.append("---#");
            } else {
                piece4.append("----#");
            }
        } else {
            piece4.append("------#");
        }

        List<CharSequence> incorrect = getIncorrectGuesses();
        ChatFormatter piece5 = ChatFormatter.empty();
        String help1 = "";
        if (!incorrect.isEmpty()) {
            help1 = "Špatně: " + String.join(", ", incorrect);
        }
        if (incorrectGuesses >= 5) {
            piece5.append("-");
            piece5.append(Component.text("/", red));
            if (incorrectGuesses >= 6) {
                piece5.append("-");
                piece5.append(Component.text("\\", red));
                piece5.append("--# ");
            } else {
                piece5.append("---# ");
            }
            piece5.append(help1);
        } else {
            piece5.append("------# ");
            piece5.append(help1);
        }

        ChatFormatter piece6 = ChatFormatter.of("#######")
            .append(" ")
            .append("/answer <písmeno/slovo>");

        return ChatFormatter.empty()
            .appendMany(
                piece1, nl,
                piece2, nl,
                piece3, nl,
                piece4, nl,
                piece5, nl,
                piece6
            )
            .build();
    }

    private void printGameState(Player player) {
        player.sendMessage(Component.text("[[[ HANGMAN ]]]", TextColor.color(30, 144, 255)));
        player.sendMessage(getHangmanState());
        player.sendMessage("Word: " + word);
    }

    @Override
    public void onHint(Player player) {
        player.sendMessage("Žádná nápověda pro tuto hru.");
    }

    @Override
    public String getGameSummary() {
        if (isWordGuessed()) {
            return "Uhádl jsi slovo '" + word + "' s " + incorrectGuesses + " špatnými pokusy!";
        } else {
            return "Nepodařilo se ti uhádnout slovo '" + word + "'. Příště se ti to možná podaří!";
        }
    }
    
    @Override
    public void start(Player player) {
        printGameState(player);
    }
    
    @Override
    public ChatGameResponse onAnswer(Player player, String answer) {
        answer = answer.trim().toUpperCase();
        
        // whole word/sentence guess
        if (answer.length() > 1) {
            if (answer.equalsIgnoreCase(word)) {
                for (char c : word.toCharArray()) {
                    guessedLetters.add(Character.toLowerCase(c));
                }
                return ChatGameResponse.endGameCorrect();
            } else {
                incorrectGuesses++;
            }
        } else { // single letter guess
            char guess = answer.charAt(0);
            
            if (guessedLetters.contains(guess)) {
                return ChatGameResponse.incorrect();
            }
            
            guessedLetters.add(guess);
            
            if (word.indexOf(guess) == -1) {
                incorrectGuesses++;
            }
        }
        
        printGameState(player);
        
        if (isWordGuessed()) {
            return ChatGameResponse.endGameCorrect();
        } else if (incorrectGuesses >= MAX_INCORRECT_GUESSES) {
            return ChatGameResponse.endGameIncorrect();
        }
        
        return ChatGameResponse.incorrect();
    }
}
