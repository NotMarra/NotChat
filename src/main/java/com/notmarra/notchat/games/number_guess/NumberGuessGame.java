package com.notmarra.notchat.games.number_guess;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notchat.utils.ChatFormatter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class NumberGuessGame extends ChatGame {
    public static String GAME_ID = "number_guess";

    private final int minNumber;
    private final int maxNumber;
    private final int maxAttempts;
    private final int targetNumber;

    private int attemptsLeft;
    private boolean gameWon;

    public enum GameDifficulty {
        EASY(1, 50, 10),
        MEDIUM(1, 100, 7),
        HARD(1, 200, 5);

        private final int minValue;
        private final int maxValue;
        private final int attemps;

        GameDifficulty(int minValue, int maxValue, int attemps) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.attemps = attemps;
        }

        public int getMinValue() {
            return minValue;
        }

        public int getMaxValue() {
            return maxValue;
        }

        public int getAttemps() {
            return attemps;
        }
    }
    
    public NumberGuessGame(JavaPlugin plugin, String id) {
        super(plugin, id);
        GameDifficulty difficulty = GameDifficulty.MEDIUM;
        this.minNumber = difficulty.getMinValue();
        this.maxNumber = difficulty.getMaxValue();
        this.maxAttempts = difficulty.getAttemps();
        this.attemptsLeft = maxAttempts;
        this.gameWon = false;

        Random random = new Random();
        this.targetNumber = random.nextInt(maxNumber - minNumber + 1) + minNumber;
    }

    public boolean isGameOver() {
        return attemptsLeft <= 0 || gameWon;
    }

    @Override
    public void onHint(Player player) {
        player.sendMessage("Myslím si číslo mezi " + minNumber + " a " + maxNumber + ".");
    }
    
    @Override
    public String getGameSummary() {
        if (gameWon) {
            return "Správně jsi uhádl číslo " + targetNumber + "!";
        } else {
            return "Nepodařilo se ti uhádnout číslo " + targetNumber + " s " + maxAttempts + " pokusy.";
        }
    }

    @Override
    public void start(Player player) {
        player.sendMessage(Component.text("[[[ NUMBER GUESS ]]]", TextColor.color(30, 144, 255)));
        player.sendMessage("Myslím si číslo mezi " + minNumber + " a " + maxNumber + ".");
        player.sendMessage("Máš " + maxAttempts + " pokusů. Jaké číslo si myslím?");

        ChatFormatter hintMessage = ChatFormatter.empty();
        hintMessage.append(Component.text("/answer <číslo>", Style.style(TextDecoration.BOLD)));
        hintMessage.append(Component.text(" nebo "));
        hintMessage.append(Component.text("/hint", Style.style(TextDecoration.BOLD)));
        hintMessage.append(Component.text(" pro nápovědu."));
        player.sendMessage(hintMessage.build());
    }

    @Override
    public ChatGameResponse onAnswer(Player player, String answer) {
        try {
            int guess = Integer.parseInt(answer.trim());
            attemptsLeft--;

            if (guess == targetNumber) {
                gameWon = true;
                return ChatGameResponse.endGameCorrect();
            }

            if (attemptsLeft <= 0) {
                return ChatGameResponse.endGameIncorrect();
            }

            ChatFormatter message = ChatFormatter.of("[" + guess + "] ");
            if (guess < targetNumber) {
                message.append(Component.text("Větší!", TextColor.color(0, 255, 0)));
            } else {
                message.append(Component.text("Menší!", TextColor.color(255, 0, 0)));
            }
            message.append(Component.text(" Zbývá "));
            message.append(Component.text(String.valueOf(attemptsLeft), TextColor.color(0, 0, 255)));
            message.append(Component.text(" pokusů."));
            player.sendMessage(message.build());

            return ChatGameResponse.incorrect();
        } catch (NumberFormatException e) {
            return ChatGameResponse.incorrect();
        }
    }
}
