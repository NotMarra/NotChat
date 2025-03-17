package com.notmarra.notchat.games.true_or_false;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuestionGenerator {
    
    private final KnowledgeBase knowledgeBase;
    private final Random random;
    
    public QuestionGenerator(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
        this.random = new Random();
    }
    
    public TrueOrFalseGame.Question generateRandomQuestion() {
        List<String> categories = knowledgeBase.getCategories();
        if (categories.isEmpty()) {
            return createFallbackQuestion();
        }
        
        String category = categories.get(random.nextInt(categories.size()));
        return generateQuestion(category);
    }
    
    public TrueOrFalseGame.Question generateQuestion(String category) {
        if (!knowledgeBase.hasCategory(category)) {
            return createFallbackQuestion();
        }
        
        boolean shouldBeTrue = random.nextBoolean();
        SubjectPredicatePair pair = generateSubjectPredicate(category, shouldBeTrue);
        String questionText = formatQuestion(pair.subject, pair.predicate, shouldBeTrue);
        TrueOrFalseGame.Question question = new TrueOrFalseGame.Question(questionText, shouldBeTrue, category);
        
        if (pair.hint != null) {
            question.setHint(pair.hint);
        }
        
        if (pair.explanation != null) {
            question.setExplanation(pair.explanation);
        }
        
        return question;
    }
    
    private SubjectPredicatePair generateSubjectPredicate(String category, boolean shouldBeTrue) {
        List<String> subjects = knowledgeBase.getSubjects(category);
        
        if (subjects.isEmpty()) {
            return new SubjectPredicatePair("unknown", "has unknown properties", null, null);
        }
        
        String name = subjects.get(random.nextInt(subjects.size()));
        String formattedSubject = formatSubject(category, name);
        Map<String, String> properties = knowledgeBase.getProperties(category, name);
        String hint = knowledgeBase.getHint(category, name);
        
        if (shouldBeTrue) {
            if (properties != null && !properties.isEmpty()) {
                List<String> propertyKeys = new ArrayList<>(properties.keySet());
                String propertyKey = propertyKeys.get(random.nextInt(propertyKeys.size()));
                String predicate = properties.get(propertyKey);
                String explanation = knowledgeBase.getExplanation(category, name, propertyKey);
                
                return new SubjectPredicatePair(formattedSubject, predicate, hint, explanation);
            }
        } else {
            List<String> falseProperties = knowledgeBase.getFalseProperties(category);
            if (falseProperties != null && !falseProperties.isEmpty()) {
                List<String> truePredicates = new ArrayList<>();
                Map<String, String> trueProps = knowledgeBase.getProperties(category, name);
                if (trueProps != null) {
                    truePredicates.addAll(trueProps.values());
                }
                
                List<String> filteredFalseProps = new ArrayList<>(falseProperties);
                for (int i = 0; i < filteredFalseProps.size(); i++) {
                    String falseProp = filteredFalseProps.get(i);
                    boolean conflict = false;
                    
                    for (String truePredicate : truePredicates) {
                        if (truePredicate.toLowerCase().contains(falseProp.toLowerCase()) ||
                            falseProp.toLowerCase().contains(truePredicate.toLowerCase())) {
                            conflict = true;
                            break;
                        }
                    }
                    
                    if (conflict) {
                        filteredFalseProps.remove(i);
                        i--;
                    }
                }
                
                if (!filteredFalseProps.isEmpty()) {
                    String falsePredicate = filteredFalseProps.get(random.nextInt(filteredFalseProps.size()));
                    return new SubjectPredicatePair(formattedSubject, falsePredicate, hint, 
                            "This is false. " + knowledgeBase.getFalseExplanation(category, name));
                }
            }
        }
        
        return new SubjectPredicatePair(formattedSubject, "is a " + category + " in Minecraft", hint, null);
    }
    
    private String formatSubject(String category, String name) {
        String formatted = name.replace('_', ' ');
        
        switch (category) {
            case "biome":
                return "the " + formatted + " biome";
            case "structure":
                return "the " + formatted;
            case "dimension":
                return "the " + formatted;
            default:
                return "a " + formatted;
        }
    }
    
    private String formatQuestion(String subject, String predicate, boolean isTrue) {
        List<String> templates = isTrue ? 
                knowledgeBase.getTrueTemplates() : 
                knowledgeBase.getFalseTemplates();
        
        if (templates == null || templates.isEmpty()) {
            return "In Minecraft, " + subject + " " + predicate + ".";
        }
        
        String template = templates.get(random.nextInt(templates.size()));
        return String.format(template, subject, predicate);
    }
    
    private TrueOrFalseGame.Question createFallbackQuestion() {
        boolean answer = random.nextBoolean();
        String questionText;
        
        if (answer) {
            questionText = "In Minecraft, diamonds are rare and valuable resources.";
        } else {
            questionText = "In Minecraft, wooden tools are more durable than diamond tools.";
        }
        
        return new TrueOrFalseGame.Question(questionText, answer, "general");
    }
    
    private static class SubjectPredicatePair {
        public final String subject;
        public final String predicate;
        public final String hint;
        public final String explanation;
        
        public SubjectPredicatePair(String subject, String predicate, String hint, String explanation) {
            this.subject = subject;
            this.predicate = predicate;
            this.hint = hint;
            this.explanation = explanation;
        }
    }
}