package com.library.service.ai;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@Primary
public class MockLlmClient implements LlmChatClient {

    private final Random random = new Random();

    @Override
    public String chat(List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return "您好！我是智能书童，有什么可以帮您的吗？";
        }
        String lastMessage = messages.get(messages.size() - 1).getContent();
        return generateMockResponse(lastMessage);
    }

    private String generateMockResponse(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("python") || lower.contains("编程")) {
            return "为您找到以下 Python 相关书籍，适合不同阶段的读者：";
        }
        if (lower.contains("管理") || lower.contains("职业") || lower.contains("瓶颈")) {
            return "针对您的职业发展需求，推荐以下管理学和职场提升类书籍：";
        }
        if (lower.contains("文学") || lower.contains("小说")) {
            return "为您精选以下文学佳作：";
        }
        if (lower.contains("热门") || lower.contains("推荐") || lower.contains("随便")) {
            return "最近热门的书籍有：";
        }
        String[] defaults = {
                "我理解您的需求，为您推荐以下书籍：",
                "根据您的描述，这些书可能适合您：",
                "为您精选了以下推荐：",
                "看看这些是否符合您的口味："
        };
        return defaults[random.nextInt(defaults.length)];
    }

}
