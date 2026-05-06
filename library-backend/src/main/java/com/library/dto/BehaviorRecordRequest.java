package com.library.dto;

import lombok.Data;

@Data
public class BehaviorRecordRequest {

    private Long bookId;
    private String behaviorType;
    private Integer duration;
    private Integer rating;
    private String sessionId;
    private String metadata;

}
