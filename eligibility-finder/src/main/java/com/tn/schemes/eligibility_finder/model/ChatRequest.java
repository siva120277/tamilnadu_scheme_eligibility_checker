package com.tn.schemes.eligibility_finder.model;

import java.util.List;

public class ChatRequest {
    private String message;
    private String targetLanguage;
    private List<String> matchedSchemes;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public List<String> getMatchedSchemes() {
        return matchedSchemes;
    }

    public void setMatchedSchemes(List<String> matchedSchemes) {
        this.matchedSchemes = matchedSchemes;
    }
}