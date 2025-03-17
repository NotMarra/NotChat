package com.notmarra.notchat.games.true_or_false;

import java.util.List;
import java.util.Map;

public interface KnowledgeBase {
    List<String> getCategories();
    
    boolean hasCategory(String category);
    
    List<String> getSubjects(String category);
    
    Map<String, String> getProperties(String category, String subject);
    
    String getHint(String category, String subject);
    
    String getExplanation(String category, String subject, String propertyKey);
    
    String getFalseExplanation(String category, String subject);
    
    List<String> getFalseProperties(String category);
    
    List<String> getTrueTemplates();
    
    List<String> getFalseTemplates();
    
    boolean addCategory(String category);
    
    boolean addSubject(String category, String subject, Map<String, String> properties);
    
    boolean addFalseProperties(String category, List<String> properties);
    
    static KnowledgeBase getInstance() {
        return TrueOrFalseKnowledge.getInstance();
    }
}