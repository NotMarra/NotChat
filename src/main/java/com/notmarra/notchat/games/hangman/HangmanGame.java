package com.notmarra.notchat.games.hangman;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.entity.Player;

import com.notmarra.notchat.NotChat;
import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notlib.utils.ChatF;
import com.notmarra.notchat.utils.MinecraftStuff;

public class HangmanGame extends ChatGame {
    public static String ID = "hangman";

    private static final int MAX_INCORRECT_GUESSES = 6;
    
    private String word;
    private Set<Character> guessedLetters;
    private int incorrectGuesses;

    private boolean minecraftBlocks;
    private boolean minecraftItems;
    private boolean minecraftMobs;
    private boolean minecraftBiomes;
    private List<String> customWords;
    
    public HangmanGame(NotChat plugin) {
        super(plugin);
        
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
            if (guessedLetters.contains(c)) {
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
            if (c != ' ' && !guessedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }
    
    private ChatF getHangmanState() {
        ChatF piece1 = ChatF.of("--#####")
            .append(" ")
            .appendBold("Uhádni slovo a nenech se oběsit!");

        ChatF piece2 = ChatF.of("--#---#");

        ChatF piece3 = ChatF.empty();
        if (incorrectGuesses >= 1) {
            piece3.append("--");
            piece3.append("O", ChatF.C_RED);
            piece3.append("---# ");
            piece3.append(getCurrentWordState());
        } else {
            piece3.append("------# ");
            piece3.append(getCurrentWordState());
        }

        ChatF piece4 = ChatF.empty();
        if (incorrectGuesses >= 2) {
            piece4.append("-");
            piece4.append("/", ChatF.C_RED);
            if (incorrectGuesses >= 4) {
                piece4.append("O\\", ChatF.C_RED);
                piece4.append("--#");
            } else if (incorrectGuesses >= 3) {
                piece4.append("O", ChatF.C_RED);
                piece4.append("---#");
            } else {
                piece4.append("----#");
            }
        } else {
            piece4.append("------#");
        }

        List<CharSequence> incorrect = getIncorrectGuesses();
        ChatF piece5 = ChatF.empty();
        String help1 = "";
        if (!incorrect.isEmpty()) {
            help1 = "Špatně: " + String.join(", ", incorrect);
        }
        if (incorrectGuesses >= 5) {
            piece5.append("-");
            piece5.append("/", ChatF.C_RED);
            if (incorrectGuesses >= 6) {
                piece5.append("-");
                piece5.append("\\", ChatF.C_RED);
                piece5.append("--# ");
            } else {
                piece5.append("---# ");
            }
            piece5.append(help1);
        } else {
            piece5.append("------# ");
            piece5.append(help1);
        }

        ChatF piece6 = ChatF.of("#######")
            .append(" ")
            .appendBold("/answer <písmeno/slovo>");

        ChatF nl = ChatF.of("\n");
        return ChatF.empty()
            .appendMany(
                piece1, nl,
                piece2, nl,
                piece3, nl,
                piece4, nl,
                piece5, nl,
                piece6
            );
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public ChatF getTitle() {
        return ChatF.ofBold("[[[ HANGMAN ]]]", ChatF.C_DODGERBLUE);
    }

    @Override
    public ChatF onHint(Player player) {
        return ChatF.of("Žádná nápověda pro tuto hru.");
    }

    @Override
    public ChatF getGameSummary() {
        if (isWordGuessed()) {
            return ChatF.of("Uhádl jsi slovo '" + word + "' s " + incorrectGuesses + " špatnými pokusy!");
        } else {
            return ChatF.of("Nepodařilo se ti uhádnout slovo '" + word + "'. Příště se ti to možná podaří!");
        }
    }
    
    @Override
    public void start(Player player) {
        getHangmanState().sendTo(player);
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
                return ChatGameResponse.endCorrect();
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
        
        getHangmanState().sendTo(player);
        
        if (isWordGuessed()) {
            return ChatGameResponse.endCorrect();
        } else if (incorrectGuesses >= MAX_INCORRECT_GUESSES) {
            return ChatGameResponse.endIncorrect();
        }
        
        return ChatGameResponse.incorrect();
    }
}
