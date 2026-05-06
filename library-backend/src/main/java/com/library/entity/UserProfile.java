package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_profile")
public class UserProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String readingLevel;

    private String preferredCategories;

    private String skillTags;

    private String readingGoals;

    private LocalDateTime lastUpdated;

    private LocalDateTime createdAt;

}
