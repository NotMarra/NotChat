package com.notmarra.notchat.games.math;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class MathGame extends ChatGame {
    public static String GAME_ID = "math";

    public MathGameProblem mathProblem;
    
    public MathGame(JavaPlugin plugin, String id) {
        super(plugin, id);
        mathProblem = new MathGameProblem(MathGameProblemDifficulty.random());
    }

    @Override
    public void onHint(Player player) {
        player.sendMessage("Žádná nápověda pro tuto hru.");
    }

    @Override
    public String getGameSummary() {
        String problem = mathProblem.getPrettyPrintedProblem();
        problem = problem.replace("?", String.valueOf(mathProblem.getCorrectAnswer()));
        return "Výsledek: " + problem;
    }

    @Override
    public void start(Player player) {
        player.sendMessage(Component.text("[[[ MATH ]]]", TextColor.color(30, 144, 255)));
        player.sendMessage("Vypočítej správně: " + mathProblem.getPrettyPrintedProblem());
        player.sendMessage("Obtížnost: " + mathProblem.getDifficulty().getName());
    }

    @Override
    public ChatGameResponse onAnswer(Player player, String answer) {
        try {
            double playerAnswer = Double.parseDouble(answer);
            double correctAnswer = mathProblem.getCorrectAnswer();
            return ChatGameResponse.endGame(playerAnswer == correctAnswer);
        } catch (NumberFormatException e) {
            return ChatGameResponse.incorrect();
        }
    }
}
