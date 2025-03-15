package com.notmarra.notchat.games.math;

import java.util.Random;

public class MathGameProblem {
    private final Random random = new Random();
    private String problemText;
    private double correctAnswer;
    private MathGameProblemDifficulty difficulty;
    
    // For creating a problem with specified difficulty
    public MathGameProblem(MathGameProblemDifficulty difficulty) {
        this.difficulty = difficulty;
        generateProblem();
    }
    
    // For creating a custom problem
    public MathGameProblem(int minValue, int maxValue, int operandCount) {
        this.difficulty = null;
        generateCustomProblem(minValue, maxValue, operandCount);
    }
    
    private void generateProblem() {
        generateCustomProblem(
            difficulty.getMinValue(),
            difficulty.getMaxValue(),
            difficulty.getOperands()
        );
    }
    
    private void generateCustomProblem(int minValue, int maxValue, int operandCount) {
        // Use a StringBuilder to build the problem text
        StringBuilder problemBuilder = new StringBuilder();
        
        // Generate the first operand
        int firstOperand = randomInt(minValue, maxValue);
        problemBuilder.append(firstOperand);
        
        // Start with the first operand as our running result
        double result = firstOperand;
        
        // For each additional operand, apply a random operation
        for (int i = 1; i < operandCount; i++) {
            // Choose operation (avoid division in complex problems to avoid fractions)
            MathGameOperation operation = MathGameOperation.randomSimple();
            
            // Generate the next operand
            int nextOperand = randomInt(minValue, maxValue);
            
            // If using subtraction and next operand would make result negative,
            // ensure the problem is solvable for early learners by swapping or adjusting
            if (operation == MathGameOperation.SUBTRACTION && nextOperand > result) {
                // For simpler problems, ensure positive results
                if (difficulty == MathGameProblemDifficulty.EASY || difficulty == MathGameProblemDifficulty.MEDIUM) {
                    nextOperand = randomInt(minValue, (int)result);
                }
            }
            
            // If using division, ensure it divides evenly for simpler problems
            if (operation == MathGameOperation.DIVISION) {
                // For easier difficulties, ensure clean division
                if (difficulty == MathGameProblemDifficulty.EASY || difficulty == MathGameProblemDifficulty.MEDIUM) {
                    // Find a divisor that works cleanly
                    int[] cleanDivisors = {2, 3, 4, 5, 10};
                    nextOperand = cleanDivisors[random.nextInt(cleanDivisors.length)];
                    // Adjust previous result to be divisible by this operand
                    result = Math.ceil(result / nextOperand) * nextOperand;
                }
            }
            
            // Append the operation and next operand to the problem text
            problemBuilder.append(" ").append(operation.getSymbol()).append(" ").append(nextOperand);
            
            // Update the running result
            result = operation.apply(result, nextOperand);
        }
        
        // Add equals sign and placeholder for the answer
        problemBuilder.append(" = ?");
        
        // Store the problem text and correct answer
        this.problemText = problemBuilder.toString();
        this.correctAnswer = result;
    }
    
    private int randomInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    public MathGameProblemDifficulty getDifficulty() {
        return difficulty;
    }
    
    public String getProblemText() {
        return problemText;
    }
    
    public double getCorrectAnswer() {
        return correctAnswer;
    }
    
    /**
     * Formats the problem with pretty-printing
     * For example: (3 + 4) × 2 = ?
     */
    public String getPrettyPrintedProblem() {
        String problem = problemText.replace("*", "×").replace("/", "÷");
        
        // Add parentheses for clarity in complex expressions
        if (difficulty != null && difficulty.getOperands() > 2) {
            // Replace simple operation patterns with parenthesized versions
            // This is a simplified approach - a proper expression parser would be better
            // for more complex formatting
            problem = problem.replaceAll("(\\d+) ([+\\-]) (\\d+) ([×÷]) (\\d+)", "($1 $2 $3) $4 $5");
        }
        
        return problem;
    }
    
    /**
     * Creates problems with parentheses to teach order of operations
     * This will create problems like: 2 + 3 * 4 or (2 + 3) * 4
     * to teach the difference in order of operations
     */
    public static MathGameProblem createOrderOfOperationsProblem(int minValue, int maxValue) {
        Random random = new Random();
        MathGameProblem problem = new MathGameProblem(MathGameProblemDifficulty.EASY); // Temporarily create a simple problem
        
        // Generate three numbers for a classic order of operations problem
        int a = minValue + random.nextInt(maxValue - minValue + 1);
        int b = minValue + random.nextInt(maxValue - minValue + 1);
        int c = minValue + random.nextInt(maxValue - minValue + 1);
        
        // Choose two operations, first one preferably + or -, second one preferably * or /
        MathGameOperation firstOp = random.nextBoolean() ? 
                MathGameOperation.ADDITION : MathGameOperation.SUBTRACTION;
        
        MathGameOperation secondOp = random.nextBoolean() ? 
                MathGameOperation.MULTIPLICATION : MathGameOperation.ADDITION;
        
        // Decide whether to add parentheses or not
        boolean useParentheses = random.nextBoolean();
        
        double correctAnswer;
        String problemText;
        
        if (useParentheses) {
            // Case with parentheses: (a firstOp b) secondOp c
            double parenthesisResult = firstOp.apply(a, b);
            correctAnswer = secondOp.apply(parenthesisResult, c);
            problemText = "(" + a + " " + firstOp.getSymbol() + " " + b + ") " + secondOp.getSymbol() + " " + c + " = ?";
        } else {
            // Case without parentheses: a firstOp b secondOp c
            // Follow standard order of operations
            if (secondOp == MathGameOperation.MULTIPLICATION || secondOp == MathGameOperation.DIVISION) {
                // Multiplication/division happens before addition/subtraction
                double secondOpResult = secondOp.apply(b, c);
                correctAnswer = firstOp.apply(a, secondOpResult);
            } else {
                // Both operations are at the same precedence level, evaluate left to right
                double firstOpResult = firstOp.apply(a, b);
                correctAnswer = secondOp.apply(firstOpResult, c);
            }
            problemText = a + " " + firstOp.getSymbol() + " " + b + " " + secondOp.getSymbol() + " " + c + " = ?";
        }
        
        // Override the problem's text and answer
        problem.problemText = problemText;
        problem.correctAnswer = correctAnswer;
        
        return problem;
    }
    
    /**
     * Creates problems with variables to solve for x
     */
    public static MathGameProblem createVariableProblem(int minValue, int maxValue) {
        Random rand = new Random();
        
        // Generate two numbers
        int a = minValue + rand.nextInt(maxValue - minValue + 1);
        int b = minValue + rand.nextInt(maxValue - minValue + 1);
        
        // Create a problem where x = a
        int result = a + b;
        
        MathGameProblem problem = new MathGameProblem(MathGameProblemDifficulty.EASY);
        problem.problemText = "x + " + b + " = " + result;
        problem.correctAnswer = a;
        
        return problem;
    }
}