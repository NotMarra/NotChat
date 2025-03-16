package com.notmarra.notchat.games.number_guess;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notchat.utils.ChatF;

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
    public ChatF getTitle() {
        return ChatF.ofBold("[[[ NUMBER GUESS ]]]", ChatF.C_DODGERBLUE);
    }

    @Override
    public ChatF onHint(Player player) {
        return ChatF.of("Myslím si číslo mezi " + minNumber + " a " + maxNumber + ".");
    }
    
    @Override
    public ChatF getGameSummary() {
        if (gameWon) {
            return ChatF.of("Správně jsi uhádl číslo " + targetNumber + "!");
        } else {
            return ChatF.of("Nepodařilo se ti uhádnout číslo " + targetNumber + " s " + maxAttempts + " pokusy.");
        }
    }

    @Override
    public void start(Player player) {
        ChatF message1 = ChatF.empty()
            .append("Myslím si číslo mezi " + minNumber + " a " + maxNumber + ".");
        player.sendMessage(message1.build());

        ChatF message2 = ChatF.empty()
            .append("Máš ")
            .appendBold(String.valueOf(maxAttempts), ChatF.C_RED)
            .append(" pokusů.")
            .append(" Jaké číslo si myslím?");
        player.sendMessage(message2.build());

        ChatF hintMessage = ChatF.empty()
            .appendBold("/answer <číslo>")
            .append(" nebo ")
            .appendBold("/hint")
            .append(" pro nápovědu.");
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

            ChatF message = ChatF.of("[" + guess + "] ");
            if (guess < targetNumber) {
                message.appendBold("Větší!", ChatF.C_GREEN);
            } else {
                message.appendBold("Menší!", ChatF.C_RED);
            }
            message.append(" ");
            message.append("Zbývá ");
            message.append(String.valueOf(attemptsLeft), ChatF.C_YELLOW);
            message.append(" pokusů.");
            player.sendMessage(message.build());

            return ChatGameResponse.incorrect();
        } catch (NumberFormatException e) {
            return ChatGameResponse.incorrect();
        }
    }
}
