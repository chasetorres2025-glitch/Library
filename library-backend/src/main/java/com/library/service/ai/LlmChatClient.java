package com.library.service.ai;

import java.util.List;

public interface LlmChatClient {

    String chat(List<ChatMessage> messages);

}
