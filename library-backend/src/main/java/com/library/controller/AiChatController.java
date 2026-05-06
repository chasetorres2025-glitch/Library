package com.library.controller;

import com.library.dto.ChatMessageRequest;
import com.library.entity.AiConversation;
import com.library.service.AiChatService;
import com.library.utils.JwtUtil;
import com.library.vo.ChatMessageResponse;
import com.library.vo.ChatSessionVo;
import com.library.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/ai/chat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/session")
    public Result<ChatSessionVo> createSession(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        ChatSessionVo session = aiChatService.createSession(userId);
        return Result.success(session);
    }

    @PostMapping("/message")
    public Result<ChatMessageResponse> sendMessage(@RequestBody ChatMessageRequest req, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error("用户未登录");
        }
        ChatMessageResponse response = aiChatService.sendMessage(userId, req.getSessionId(), req.getMessage());
        return Result.success(response);
    }

    @GetMapping("/history")
    public Result<List<AiConversation>> getHistory(@RequestParam String sessionId, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error("用户未登录");
        }
        List<AiConversation> history = aiChatService.getHistory(userId, sessionId);
        return Result.success(history);
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
