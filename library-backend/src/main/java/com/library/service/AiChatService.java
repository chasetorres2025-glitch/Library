package com.library.service;

import com.library.entity.AiConversation;
import com.library.vo.ChatMessageResponse;
import com.library.vo.ChatSessionVo;

import java.util.List;

public interface AiChatService {

    ChatSessionVo createSession(Long userId);

    ChatMessageResponse sendMessage(Long userId, String sessionId, String message);

    List<AiConversation> getHistory(Long userId, String sessionId);

}
