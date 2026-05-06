package com.library.service.ai;

import lombok.Data;

import java.util.List;

@Data
public class IntentResult {

    private String intentType;
    private double confidence;
    private List<String> keywords;
    private String rawQuery;

    public IntentResult() {
    }

    public IntentResult(String intentType, double confidence, List<String> keywords, String rawQuery) {
        this.intentType = intentType;
        this.confidence = confidence;
        this.keywords = keywords;
        this.rawQuery = rawQuery;
    }

}
