package com.notmarra.notchat.games.math;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.notmarra.notchat.games.ChatGame;
import com.notmarra.notchat.games.ChatGameResponse;
import com.notmarra.notchat.utils.ChatF;

public class MathGame extends ChatGame {
    public static String GAME_ID = "math";

    public MathGameProblem mathProblem;
    
    public MathGame(JavaPlugin plugin, String id) {
        super(plugin, id);
        mathProblem = new MathGameProblem(MathGameProblemDifficulty.random());
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
        ChatF problem = ChatF.empty()
            .append("Vypočítej správně: ")
            .append(mathProblem.getPrettyPrintedProblem());
        player.sendMessage(problem.build());

        ChatF difficulty = ChatF.empty()
            .append("Obtížnost: ")
            .append(mathProblem.getDifficulty().getName());
        player.sendMessage(difficulty.build());
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
