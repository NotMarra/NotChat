package com.notmarra.notchat.games;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MathGame extends ChatGame<MathGameResponse> {
    private final int min;
    private final int max;

    public MathGame(String id) {
        super(id);
        ConfigurationSection settings = getSettings();
        this.min = settings.getInt("min_number", 1);
        this.max = settings.getInt("max_number", 1000);
    }

    @Override
    public String generateProblem() {
        int num1 = (int) (Math.random() * (max - min + 1)) + min;
        int num2 = (int) (Math.random() * (max - min + 1)) + min;
        int operation = (int) (Math.random() * 4);

        return switch (operation) {
            case 1 -> num1 + " - " + num2 + " = ?";
            case 2 -> num1 + " * " + num2 + " = ?";
            case 3 -> num1 + " / " + num2 + " = ?";
            default -> num1 + " + " + num2 + " = ?";
        };
    }

    @Override
    public MathGameResponse onAnswer(Player player, String answer) {
        MathGameResponse response = new MathGameResponse();
        try {
            int playerAnswer = Integer.parseInt(answer);
            int correctAnswer = calculateCorrectAnswer();
            response.setCorrect(playerAnswer == correctAnswer);
        } catch (NumberFormatException e) {
            response.setCorrect(false);
        }
        return response;
    }

    private int calculateCorrectAnswer(int num1, int num2, int operation) {
        return switch (operation) {
            case 1 -> num1 - num2;
            case 2 -> num1 * num2;
            case 3 -> num1 / num2;
            default -> num1 + num2;
        };
    }
}
