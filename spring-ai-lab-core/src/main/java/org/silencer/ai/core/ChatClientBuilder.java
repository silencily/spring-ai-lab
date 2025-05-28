package org.silencer.ai.core;

import com.google.common.collect.Maps;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChatClientBuilder {
    private final Map<LlmProvider, LlmModelFactory> factories;
    private final List<Advisor> defaultAdvisors;
    private final List<ToolCallback> defaultTools;

    public ChatClientBuilder(List<LlmModelFactory> factories, List<Advisor> defaultAdvisors,
                             List<ToolCallback> defaultTools) {
        if (factories == null) {
            this.factories = Maps.newHashMap();
        } else {
            this.factories = factories.stream().collect(Collectors.toMap(LlmModelFactory::provider, Function.identity()));
        }
        this.defaultAdvisors = defaultAdvisors;
        this.defaultTools = defaultTools;
    }

    public LlmModelFactory getLlmModelFactory(LlmProvider provider) {
        return factories.get(provider);
    }

    public ChatClient buildChatClient(LlmConfig llmConfig) {
        LlmModelFactory factory = getLlmModelFactory(llmConfig.getProvider());
        ChatModel chatModel = factory.createChatModel(llmConfig);
        return ChatClient.builder(chatModel)
                .defaultAdvisors(defaultAdvisors)
                .defaultTools(defaultTools)
                .build();
    }
}
