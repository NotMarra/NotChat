package com.notmarra.notchat.games;

import org.bukkit.entity.Player;

public class MathGame extends ChatGame<MathGameResponse> {
    private int min

    public MathGame(String id) {
        super(id);
    }

    @Override
    public MathGameResponse onAnswer(Player player, String answer) {
        return null;
    }

    public int min() {
        return 0;
    }

    public int max() {
        return 1000;
    }
}
