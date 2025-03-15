package com.notmarra.notchat.games;

public class ChatGameResponse {
    public boolean correct;
    public boolean endingGame;

    public ChatGameResponse(boolean correct, boolean endingGame) {
        this.correct = correct;
        this.endingGame = endingGame;
    }

    public static ChatGameResponse endGame(boolean correct) {
        return new ChatGameResponse(correct, true);
    }

    // nastavení správné odpovědi, která zároveň ukončí hru
    public static ChatGameResponse endGameCorrect() {
        return new ChatGameResponse(true, true);
    }

    // nastavení nesprávné odpovědi, která zároveň ukončí hru
    public static ChatGameResponse endGameIncorrect() {
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
}
