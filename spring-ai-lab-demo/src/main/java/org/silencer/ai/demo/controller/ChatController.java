package org.silencer.ai.demo.controller;

import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.silencer.ai.core.ChatClientBuilder;
import org.silencer.ai.demo.mapper.ChatResponseMapper;
import org.silencer.ai.demo.model.ChatCompletionRequest;
import org.silencer.ai.demo.model.ChatCompletionResponse;
import org.silencer.ai.demo.model.ChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ChatController.API_URI_BASE)
public class ChatController {
    public static final String API_URI_BASE = "/v1/chat/";

    @Autowired
    private ChatClientBuilder chatClientBuilder;
    @Autowired
    private ChatResponseMapper chatResponseMapper;

    @PostMapping("/completions")
    public Flux<ChatCompletionResponse> completions(@RequestBody @Valid ChatCompletionRequest chatCompletionRequest) {
        ChatOptions chatOptions = chatCompletionRequest.getChatOptions();
        ChatClient chatClient = chatClientBuilder.buildChatClient(chatOptions.buildLlmConfig());
        List<Message> messages = chatCompletionRequest.getMessages().stream()
                .map(message -> {
                    switch (message.getRole()) {
                        case USER -> {
                            return new UserMessage(message.getContent());
                        }
                        case SYSTEM -> {
                            return new SystemMessage(message.getContent());
                        }
                        case ASSISTANT -> {
                            return new AssistantMessage(message.getContent());
                        }
                    }
                    throw new RuntimeException(String.format("Unsupported message role:[%s]", message.getRole()));
                })
                .collect(Collectors.toList());
//        org.springframework.ai.chat.prompt.ChatOptions customChatOptions = ToolCallingChatOptions.builder()
//                .model(clientOptions.getModel())
//                .frequencyPenalty(clientOptions.getFrequencyPenalty())
//                .maxTokens(clientOptions.getMaxTokens())
//                .presencePenalty(clientOptions.getPresencePenalty())
//                .stopSequences(clientOptions.getStopSequences())
//                .temperature(clientOptions.getTemperature())
//                .topK(clientOptions.getTopK())
//                .topP(clientOptions.getTopP())
//                .build();

        Prompt prompt = new Prompt(messages);
        if (!chatCompletionRequest.isStream()) {
            return Flux.just(chatResponseMapper.toChatCompletion(chatClient.prompt(prompt).advisors(advisor -> {
                if (StringUtils.isNotBlank(chatCompletionRequest.getConversationId())) {
//                    advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatCompletionRequest.getConversationId());
                }
            }).call().chatResponse()));
        }
        return chatClient.prompt(prompt).advisors(advisor -> {
            if (StringUtils.isNotBlank(chatCompletionRequest.getConversationId())) {
//                advisor.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatCompletionRequest.getConversationId());
            }
        }).stream().chatResponse().map(chatResponseMapper::toChatCompletion);
    }
}
