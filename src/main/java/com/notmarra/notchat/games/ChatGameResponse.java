package com.notmarra.notchat.games;

import java.util.ArrayList;
import java.util.List;

public class ChatGameResponse {
    public boolean correct;
    public boolean endingGame;
    public List<String> rewardCommands;

    public ChatGameResponse(boolean correct, boolean endingGame) {
        this.correct = correct;
        this.endingGame = endingGame;
        this.rewardCommands = new ArrayList<>();
    }

    public static ChatGameResponse end(boolean correct) {
        return new ChatGameResponse(correct, true);
    }

    // nastavení správné odpovědi, která zároveň ukončí hru
    public static ChatGameResponse endCorrect() {
        return new ChatGameResponse(true, true);
    }

    // nastavení nesprávné odpovědi, která zároveň ukončí hru
    public static ChatGameResponse endIncorrect() {
        return new ChatGameResponse(false, true);
    }

    // nastavení správné odpovědi
    public static ChatGameResponse correct() {
        return new ChatGameResponse(true, false);
    }

    // nastavení nesprávné odpovědi
    public static ChatGameResponse incorrect() {
        return new ChatGameResponse(false, false);
    }

    public ChatGameResponse withRewardCommands(List<String> commands) {
        this.rewardCommands.addAll(commands);
        return this;
    }
}
