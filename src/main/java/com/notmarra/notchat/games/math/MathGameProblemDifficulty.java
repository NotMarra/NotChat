package com.notmarra.notchat.games.math;

import java.util.Random;

public enum MathGameProblemDifficulty {
    EASY("Easy", 2, 1, 10),
    MEDIUM("Medium", 3, 1, 20),
    HARD("Hard", 4, 1, 50),
    EXPERT("Expert", 5, 1, 100);
    
    private final String name;
    private final int operands;
    private final int minValue;
    private final int maxValue;
    
    MathGameProblemDifficulty(String name, int operands, int minValue, int maxValue) {
        this.name = name;
        this.operands = operands;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getName() {
        return name;
    }
    
    public int getOperands() {
        return operands;
    }
    
    public int getMinValue() {
        return minValue;
    }
    
    public int getMaxValue() {
        return maxValue;
    }

    public static MathGameProblemDifficulty random() {
        MathGameProblemDifficulty[] values = MathGameProblemDifficulty.values();
        return values[new Random().nextInt(values.length)];
    }
}