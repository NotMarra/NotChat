package com.notmarra.notchat.games.math;

import java.util.Random;
import java.util.function.BiFunction;

public enum MathGameOperation {
    ADDITION("+", (a, b) -> a + b),
    SUBTRACTION("-", (a, b) -> a - b),
    MULTIPLICATION("*", (a, b) -> a * b),
    DIVISION("/", (a, b) -> a / b),
    EXPONENTIATION("^", (a, b) -> Math.pow(a, b)),
    MODULO("%", (a, b) -> a % b);

    private final String symbol;
    private final BiFunction<Double, Double, Double> operation;

    MathGameOperation(String symbol, BiFunction<Double, Double, Double> operation) {
        this.symbol = symbol;
        this.operation = operation;
    }

    public String getSymbol() {
        return symbol;
    }

    public double apply(double a, double b) {
        return operation.apply(a, b);
    }

    public static MathGameOperation random() {
        MathGameOperation[] values = MathGameOperation.values();
        return values[new Random().nextInt(values.length)];
    }
    
    public static MathGameOperation randomSimple() {
        MathGameOperation[] values = {ADDITION, SUBTRACTION, MULTIPLICATION};
        return values[new Random().nextInt(values.length)];
    }
}