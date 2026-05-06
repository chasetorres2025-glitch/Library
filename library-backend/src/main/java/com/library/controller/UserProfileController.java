package com.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.dto.ProfileUpdateRequest;
import com.library.entity.BookBorrow;
import com.library.entity.UserBehavior;
import com.library.entity.UserProfile;
import com.library.mapper.BookBorrowMapper;
import com.library.mapper.UserBehaviorMapper;
import com.library.service.UserProfileService;
import com.library.utils.JwtUtil;
import com.library.vo.ProfileStatisticsVo;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BookBorrowMapper bookBorrowMapper;

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public Result<UserProfile> getProfile(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error("用户未登录");
        }
        UserProfile profile = userProfileService.getOrCreateByUserId(userId);
        return Result.success(profile);
    }

    @PutMapping
    public Result<String> updateProfile(@RequestBody ProfileUpdateRequest req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error("用户未登录");
        }
        userProfileService.updateProfile(userId, req.getReadingLevel(), req.getPreferredCategories(), req.getSkillTags(), req.getReadingGoals());
        return Result.success("更新成功");
    }

    @GetMapping("/statistics")
    public Result<ProfileStatisticsVo> getStatistics(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error("用户未登录");
        }

        UserProfile profile = userProfileService.getOrCreateByUserId(userId);

        LambdaQueryWrapper<BookBorrow> borrowWrapper = new LambdaQueryWrapper<>();
        borrowWrapper.eq(BookBorrow::getUserId, userId);
        List<BookBorrow> borrows = bookBorrowMapper.selectList(borrowWrapper);

        int totalBorrowed = borrows.size();
        int readingNow = (int) borrows.stream().filter(b -> b.getStatus() != null && b.getStatus() == 0).count();
        int completed = (int) borrows.stream().filter(b -> b.getStatus() != null && b.getStatus() == 1).count();

        LambdaQueryWrapper<UserBehavior> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(UserBehavior::getUserId, userId).eq(UserBehavior::getBehaviorType, "favorite");
        int favorited = Math.toIntExact(userBehaviorMapper.selectCount(favoriteWrapper));

        ProfileStatisticsVo vo = new ProfileStatisticsVo();
        vo.setUserId(userId);
        vo.setReadingLevel(profile.getReadingLevel());
        vo.setPreferredCategories(profile.getPreferredCategories());
        vo.setSkillTags(profile.getSkillTags());
        vo.setReadingGoals(profile.getReadingGoals());
        vo.setTotalBorrowed(totalBorrowed);
        vo.setReadingNow(readingNow);
        vo.setCompleted(completed);
        vo.setFavorited(favorited);

        String level = profile.getReadingLevel();
        if ("advanced".equals(level)) {
            vo.setSuggestion("您可以尝试跨学科阅读，挑战更高难度的专业书籍");
        } else if ("intermediate".equals(level)) {
            vo.setSuggestion("可以尝试高级技术书籍和跨学科阅读，拓宽知识面");
        } else {
            vo.setSuggestion("建议从入门书籍开始，循序渐进地提升阅读难度");
        }

        return Result.success(vo);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    return jwtUtil.getUserIdFromToken(token);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
