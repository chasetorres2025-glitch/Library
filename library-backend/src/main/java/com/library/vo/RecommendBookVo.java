package com.library.vo;

import lombok.Data;

@Data
public class RecommendBookVo {

    private Long bookId;
    private String bookName;
    private String author;
    private String coverUrl;
    private Double score;
    private String reason;

}
