package com.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.library.entity.UserProfile;

public interface UserProfileService extends IService<UserProfile> {

    UserProfile getOrCreateByUserId(Long userId);

    void updateProfile(Long userId, String readingLevel, String preferredCategories, String skillTags, String readingGoals);

}
