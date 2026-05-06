package com.library.vo;

import lombok.Data;

@Data
public class ProfileStatisticsVo {

    private Long userId;
    private String readingLevel;
    private String preferredCategories;
    private String skillTags;
    private String readingGoals;

    private Integer totalBorrowed;
    private Integer readingNow;
    private Integer completed;
    private Integer favorited;

    private String suggestion;

}
