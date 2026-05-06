package com.library.service.ai;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class RuleBasedIntentRecognizer implements IntentRecognizer {

    private static final String SKILL_LEARNING = "skill_learning";
    private static final String PROBLEM_SOLVING = "problem_solving";
    private static final String LITERATURE_READING = "literature_reading";
    private static final String CASUAL_BROWSING = "casual_browsing";
    private static final String GENERAL_INQUIRY = "general_inquiry";

    private static final Map<String, List<String>> INTENT_KEYWORDS = new LinkedHashMap<>();

    static {
        INTENT_KEYWORDS.put(SKILL_LEARNING, Arrays.asList(
                "学", "入门", "教程", "python", "java", "编程", "开发", "数据", "算法",
                "前端", "后端", "web", "app", "人工智能", "机器学习", "深度学习",
                "学一下", "想学", "怎么学", "如何学", "基础", "进阶", "高级"
        ));
        INTENT_KEYWORDS.put(PROBLEM_SOLVING, Arrays.asList(
                "瓶颈", "问题", "提升", "管理", "沟通", "职业", "职场", "困难",
                "怎么办", "如何解决", "建议", "帮助", "迷茫", "焦虑", "压力",
                "领导力", "团队", "效率", "时间", "规划"
        ));
        INTENT_KEYWORDS.put(LITERATURE_READING, Arrays.asList(
                "文学", "小说", "哲学", "人生", "经典", "名著", "散文", "诗歌",
                "治愈", "励志", "感动", "深刻", "意义", "活着", "爱情", "历史",
                "想看", "阅读", "精神", "心灵"
        ));
        INTENT_KEYWORDS.put(CASUAL_BROWSING, Arrays.asList(
                "热门", "推荐", "随便", "看看", "新书", "畅销", "排行", "榜单",
                "最近", "火", " popular", " trending", "有什么", "啥书"
        ));
    }

    @Override
    public IntentResult recognize(String message) {
        if (message == null || message.trim().isEmpty()) {
            return new IntentResult(GENERAL_INQUIRY, 0.5, Collections.emptyList(), message);
        }

        String lowerMsg = message.toLowerCase();
        String bestIntent = GENERAL_INQUIRY;
        int bestScore = 0;
        List<String> matchedKeywords = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : INTENT_KEYWORDS.entrySet()) {
            int score = 0;
            List<String> keywords = new ArrayList<>();
            for (String keyword : entry.getValue()) {
                if (lowerMsg.contains(keyword.toLowerCase())) {
                    score++;
                    keywords.add(keyword);
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestIntent = entry.getKey();
                matchedKeywords = keywords;
            }
        }

        double confidence = Math.min(0.5 + bestScore * 0.15, 0.95);
        if (bestScore == 0) {
            confidence = 0.5;
        }

        return new IntentResult(bestIntent, confidence, matchedKeywords, message);
    }

}
