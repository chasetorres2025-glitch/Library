package com.library.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChatMessageResponse {

    private String role;
    private String content;
    private String intentType;
    private Double intentConfidence;
    private List<RecommendBookVo> recommendations;
    private List<String> suggestions;

}
