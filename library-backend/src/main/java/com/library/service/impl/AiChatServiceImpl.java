package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.library.entity.AiConversation;
import com.library.entity.BookInfo;
import com.library.entity.BookTag;
import com.library.mapper.AiConversationMapper;
import com.library.mapper.BookInfoMapper;
import com.library.mapper.BookTagMapper;
import com.library.service.AiChatService;
import com.library.service.RecommendationService;
import com.library.service.ai.ChatMessage;
import com.library.service.ai.IntentRecognizer;
import com.library.service.ai.IntentResult;
import com.library.service.ai.LlmChatClient;
import com.library.vo.ChatMessageResponse;
import com.library.vo.ChatSessionVo;
import com.library.vo.QuickActionVo;
import com.library.vo.RecommendBookVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiConversationMapper aiConversationMapper;

    @Autowired
    private IntentRecognizer intentRecognizer;

    @Autowired
    private LlmChatClient llmChatClient;

    @Autowired
    private BookInfoMapper bookInfoMapper;

    @Autowired
    private BookTagMapper bookTagMapper;

    @Autowired
    private RecommendationService recommendationService;

    @Override
    public ChatSessionVo createSession(Long userId) {
        String sessionId = "chat_" + System.currentTimeMillis() + "_" + new Random().nextInt(10000);

        ChatSessionVo vo = new ChatSessionVo();
        vo.setSessionId(sessionId);
        vo.setWelcomeMessage("您好！我是智能书童，可以帮您：\n• 根据您的需求推荐书籍\n• 解答阅读相关问题\n• 制定学习计划\n\n您想了解什么？");
        vo.setQuickActions(Arrays.asList(
                new QuickActionVo("skill", "技能学习", "\uD83D\uDCDA"),
                new QuickActionVo("problem", "问题求助", "\uD83D\uDCA1"),
                new QuickActionVo("literature", "文学推荐", "\uD83D\uDCD6"),
                new QuickActionVo("casual", "随便看看", "\uD83C\uDFB2")
        ));

        AiConversation welcome = new AiConversation();
        welcome.setUserId(userId);
        welcome.setSessionId(sessionId);
        welcome.setRole("assistant");
        welcome.setContent(vo.getWelcomeMessage());
        welcome.setCreatedAt(LocalDateTime.now());
        aiConversationMapper.insert(welcome);

        return vo;
    }

    @Override
    public ChatMessageResponse sendMessage(Long userId, String sessionId, String message) {
        AiConversation userMsg = new AiConversation();
        userMsg.setUserId(userId);
        userMsg.setSessionId(sessionId);
        userMsg.setRole("user");
        userMsg.setContent(message);
        userMsg.setCreatedAt(LocalDateTime.now());
        aiConversationMapper.insert(userMsg);

        IntentResult intent = intentRecognizer.recognize(message);

        List<RecommendBookVo> recommendations = findBooksByIntent(intent);

        List<ChatMessage> history = buildLlmHistory(userId, sessionId);
        history.add(new ChatMessage("user", message));
        String reply = llmChatClient.chat(history);

        if (recommendations != null && !recommendations.isEmpty()) {
            reply = reply + "\n\n为您推荐以下书籍：";
        }

        ChatMessageResponse response = new ChatMessageResponse();
        response.setRole("assistant");
        response.setContent(reply);
        response.setIntentType(intent.getIntentType());
        response.setIntentConfidence(intent.getConfidence());
        response.setRecommendations(recommendations);
        response.setSuggestions(generateSuggestions(intent.getIntentType()));

        String recommendJson = recommendationsToJson(recommendations);

        AiConversation assistantMsg = new AiConversation();
        assistantMsg.setUserId(userId);
        assistantMsg.setSessionId(sessionId);
        assistantMsg.setRole("assistant");
        assistantMsg.setContent(reply);
        assistantMsg.setIntentType(intent.getIntentType());
        assistantMsg.setIntentConfidence(BigDecimal.valueOf(intent.getConfidence()));
        assistantMsg.setRecommendResult(recommendJson);
        assistantMsg.setCreatedAt(LocalDateTime.now());
        aiConversationMapper.insert(assistantMsg);

        return response;
    }

    @Override
    public List<AiConversation> getHistory(Long userId, String sessionId) {
        LambdaQueryWrapper<AiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiConversation::getUserId, userId)
                .eq(AiConversation::getSessionId, sessionId)
                .orderByAsc(AiConversation::getCreatedAt);
        return aiConversationMapper.selectList(wrapper);
    }

    private List<RecommendBookVo> findBooksByIntent(IntentResult intent) {
        String type = intent.getIntentType();
        List<String> keywords = intent.getKeywords();

        if ("casual_browsing".equals(type) || "general_inquiry".equals(type)) {
            return recommendationService.getPopularBooks(4);
        }

        List<BookInfo> allBooks = bookInfoMapper.selectList(null);
        List<RecommendBookVo> results = new ArrayList<>();

        for (BookInfo book : allBooks) {
            double score = 0.0;
            List<BookTag> tags = bookTagMapper.selectTagsByBookId(book.getId());
            Set<String> tagNames = tags.stream().map(BookTag::getTagName).collect(Collectors.toSet());
            String lowerName = book.getBookName().toLowerCase();
            String lowerAuthor = book.getAuthor() != null ? book.getAuthor().toLowerCase() : "";
            String lowerDesc = book.getDescription() != null ? book.getDescription().toLowerCase() : "";

            for (String kw : keywords) {
                String lowerKw = kw.toLowerCase();
                if (lowerName.contains(lowerKw)) score += 0.3;
                if (lowerAuthor.contains(lowerKw)) score += 0.2;
                if (lowerDesc.contains(lowerKw)) score += 0.2;
                if (tagNames.contains(kw)) score += 0.3;
            }

            if (score > 0) {
                RecommendBookVo vo = new RecommendBookVo();
                vo.setBookId(book.getId());
                vo.setBookName(book.getBookName());
                vo.setAuthor(book.getAuthor());
                vo.setCoverUrl(book.getCoverUrl());
                vo.setScore(score);
                vo.setReason("匹配关键词：" + String.join("、", keywords));
                results.add(vo);
            }
        }

        results.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return results.stream().limit(4).collect(Collectors.toList());
    }

    private List<ChatMessage> buildLlmHistory(Long userId, String sessionId) {
        List<AiConversation> history = getHistory(userId, sessionId);
        List<ChatMessage> messages = new ArrayList<>();
        for (AiConversation conv : history) {
            messages.add(new ChatMessage(conv.getRole(), conv.getContent()));
        }
        return messages;
    }

    private List<String> generateSuggestions(String intentType) {
        if ("skill_learning".equals(intentType)) {
            return Arrays.asList("您目前是什么水平？", "想找实战项目多的书吗？");
        } else if ("problem_solving".equals(intentType)) {
            return Arrays.asList("能具体说说您遇到的问题吗？", "您希望短期内见效还是长期提升？");
        } else if ("literature_reading".equals(intentType)) {
            return Arrays.asList("您喜欢哪个国家的文学作品？", "想看长篇还是短篇？");
        }
        return Arrays.asList("还有什么我可以帮您的吗？", "需要更具体的推荐吗？");
    }

    private String recommendationsToJson(List<RecommendBookVo> recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < recommendations.size(); i++) {
            RecommendBookVo vo = recommendations.get(i);
            sb.append("{\"bookId\":\"").append(vo.getBookId()).append("\",")
                    .append("\"bookName\":\"").append(vo.getBookName()).append("\",")
                    .append("\"author\":\"").append(vo.getAuthor()).append("\",")
                    .append("\"reason\":\"").append(vo.getReason()).append("\"}");
            if (i < recommendations.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

}
