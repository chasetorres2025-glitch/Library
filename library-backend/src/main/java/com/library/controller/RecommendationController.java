package com.library.controller;

import com.library.service.RecommendationService;
import com.library.utils.JwtUtil;
import com.library.vo.RecommendBookVo;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/personal")
    public Result<List<RecommendBookVo>> personal(@RequestParam(defaultValue = "10") int limit,
                                                   HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<RecommendBookVo> list = recommendationService.getPersonalRecommendations(userId, limit);
        return Result.success(list);
    }

    @GetMapping("/similar/{bookId}")
    public Result<Map<String, Object>> similar(@PathVariable Long bookId,
                                                 @RequestParam(defaultValue = "5") int limit) {
        List<RecommendBookVo> list = recommendationService.getSimilarBooks(bookId, limit);
        Map<String, Object> data = new HashMap<>();
        data.put("baseBookId", bookId);
        data.put("similarBooks", list);
        return Result.success(data);
    }

    @GetMapping("/popular")
    public Result<List<RecommendBookVo>> popular(@RequestParam(defaultValue = "10") int limit) {
        List<RecommendBookVo> list = recommendationService.getPopularBooks(limit);
        return Result.success(list);
    }

    @GetMapping("/recent")
    public Result<List<RecommendBookVo>> recent(@RequestParam(defaultValue = "10") int limit) {
        List<RecommendBookVo> list = recommendationService.getRecentBooks(limit);
        return Result.success(list);
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
