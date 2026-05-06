package com.library.service.ai;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文心一言 LLM 客户端预留实现
 * 如需接入，需配置 wenxin.api.key 和 wenxin.api.secret，实现真实 HTTP 调用
 * 配置 spring.ai.wenxin.enabled=true 启用
 */
@Component
@ConditionalOnProperty(prefix = "spring.ai.wenxin", name = "enabled", havingValue = "true")
public class WenxinLlmClient implements LlmChatClient {

    @Override
    public String chat(List<ChatMessage> messages) {
        // TODO: 接入文心一言 API（需申请 API Key）
        // 1. 构建请求体（符合文心一言接口规范）
        // 2. 发送 HTTP POST 请求
        // 3. 解析响应并返回文本内容
        throw new UnsupportedOperationException("文心一言客户端尚未配置，请先在 application.yml 中配置 wenxin.api.key");
    }

}
