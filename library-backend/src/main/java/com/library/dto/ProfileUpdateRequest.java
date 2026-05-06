package com.library.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {

    private String readingLevel;
    private String preferredCategories;
    private String skillTags;
    private String readingGoals;

}
