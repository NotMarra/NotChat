package com.notmarra.notchat.games.math;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notlib.extensions.NotPlugin;
import com.notmarra.notlib.utils.ChatF;

public class MathGame extends ChatGame {
    public static String ID = "math";

    public MathGameProblem mathProblem;
    
    public MathGame(NotPlugin plugin, FileConfiguration gamesConfig) {
        super(plugin, gamesConfig);

        mathProblem = new MathGameProblem(MathGameProblemDifficulty.random());
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public ChatF getTitle() {
        return ChatF.ofBold("[[[ MATH ]]]", ChatF.C_DODGERBLUE);
    }

    @Override
    public ChatF onHint(Player player) {
        return ChatF.of("Žádná nápověda pro tuto hru.");
    }

    @Override
    public ChatF getGameSummary() {
        String problem = mathProblem.getPrettyPrintedProblem();
        problem = problem.replace("?", String.valueOf(mathProblem.getCorrectAnswer()));
        return ChatF.ofBold("Výsledek: ").append(problem);
    }

    @Override
    public void start(Player player) {
        ChatF.empty()
            .append("Vypočítej správně: ")
            .append(mathProblem.getPrettyPrintedProblem())
            .sendTo(player);

        ChatF.empty()
            .append("Obtížnost: ")
            .append(mathProblem.getDifficulty().getName())
            .sendTo(player);
    }

    @Override
    public ChatGameResponse onAnswer(Player player, String answer) {
        try {
            double playerAnswer = Double.parseDouble(answer);
            double correctAnswer = mathProblem.getCorrectAnswer();
            return ChatGameResponse.end(playerAnswer == correctAnswer);
        } catch (NumberFormatException e) {
            return ChatGameResponse.incorrect();
        }
    }
}
