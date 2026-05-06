package com.library.controller;

import com.library.dto.BehaviorRecordRequest;
import com.library.entity.UserBehavior;
import com.library.service.UserBehaviorService;
import com.library.utils.JwtUtil;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/behavior")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public Result<String> record(@RequestBody BehaviorRecordRequest request, HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        if (userId == null) {
            return Result.error("用户未登录");
        }

        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setBookId(request.getBookId());
        behavior.setBehaviorType(request.getBehaviorType());
        behavior.setDuration(request.getDuration());
        behavior.setRating(request.getRating());
        behavior.setSessionId(request.getSessionId());
        behavior.setMetadata(request.getMetadata());
        behavior.setCreatedAt(LocalDateTime.now());

        userBehaviorService.save(behavior);
        return Result.success("记录成功");
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
