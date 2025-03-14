package com.notmarra.notchat.games;

public class MathGameResponse extends ChatGameResponse {
    private boolean correct;

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
