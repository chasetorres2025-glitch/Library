package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.UserProfile;
import com.library.mapper.UserProfileMapper;
import com.library.service.UserProfileService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements UserProfileService {

    @Override
    public UserProfile getOrCreateByUserId(Long userId) {
        LambdaQueryWrapper<UserProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfile::getUserId, userId);
        UserProfile profile = getOne(wrapper);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setReadingLevel("beginner");
            profile.setPreferredCategories("[]");
            profile.setSkillTags("[]");
            profile.setReadingGoals("{}");
            profile.setCreatedAt(LocalDateTime.now());
            profile.setLastUpdated(LocalDateTime.now());
            save(profile);
        }
        return profile;
    }

    @Override
    public void updateProfile(Long userId, String readingLevel, String preferredCategories, String skillTags, String readingGoals) {
        UserProfile profile = getOrCreateByUserId(userId);
        if (readingLevel != null) {
            profile.setReadingLevel(readingLevel);
        }
        if (preferredCategories != null) {
            profile.setPreferredCategories(preferredCategories);
        }
        if (skillTags != null) {
            profile.setSkillTags(skillTags);
        }
        if (readingGoals != null) {
            profile.setReadingGoals(readingGoals);
        }
        profile.setLastUpdated(LocalDateTime.now());
        updateById(profile);
    }

}
