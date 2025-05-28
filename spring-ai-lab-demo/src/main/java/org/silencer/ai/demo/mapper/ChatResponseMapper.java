package org.silencer.ai.demo.mapper;

import org.silencer.ai.demo.model.ChatCompletionChoice;
import org.silencer.ai.demo.model.ChatCompletionMessage;
import org.silencer.ai.demo.model.ChatCompletionResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatResponseMapper {

    public ChatCompletionResponse toChatCompletion(ChatResponse chatResponse) {
        ChatResponseMetadata metadata = chatResponse.getMetadata();
        String id = metadata.getId();
        Usage usage = metadata.getUsage();
        ChatCompletionResponse.Usage responseUsage = ChatCompletionResponse.Usage.builder()
                .completionTokens(usage.getCompletionTokens())
                .promptTokens(usage.getPromptTokens())
                .totalTokens(usage.getTotalTokens())
                .build();
        RateLimit rateLimit = metadata.getRateLimit();
        ChatCompletionResponse.RateLimit responseRateLimit = ChatCompletionResponse.RateLimit.builder()
                .requestsLimit(rateLimit.getRequestsLimit())
                .tokensLimit(rateLimit.getTokensLimit())
                .requestsRemaining(rateLimit.getRequestsRemaining())
                .requestsReset(rateLimit.getRequestsReset())
                .tokensRemaining(rateLimit.getTokensRemaining())
                .tokensReset(rateLimit.getTokensReset())
                .build();
        List<Generation> generations = chatResponse.getResults();
        List<ChatCompletionChoice> choices = generations.stream()
                .map(generation -> {
                    AssistantMessage assistantMessage = generation.getOutput();
                    ChatCompletionMessage message = ChatCompletionMessage.builder()
                            .role(assistantMessage.getMessageType())
                            .content(assistantMessage.getText())
                            .build();
                    return ChatCompletionChoice.builder()
                            .message(message)
                            .finishReason(generation.getMetadata().getFinishReason())
                            .build();
                })
                .collect(Collectors.toList());
        return ChatCompletionResponse.builder()
                .id(id)
                .choices(choices)
                .rateLimit(responseRateLimit)
                .usage(responseUsage)
                .build();
    }

}
