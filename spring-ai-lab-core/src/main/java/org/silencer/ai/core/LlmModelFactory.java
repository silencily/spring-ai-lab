package org.silencer.ai.core;

import org.springframework.ai.chat.model.ChatModel;

public interface LlmModelFactory {

    LlmProvider provider();

    ChatModel createChatModel(LlmConfig config);
}
