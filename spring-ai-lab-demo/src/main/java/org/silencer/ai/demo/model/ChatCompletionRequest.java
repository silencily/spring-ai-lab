package org.silencer.ai.demo.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatCompletionRequest {
    @Tolerate
    public ChatCompletionRequest() {
    }

    private String conversationId;//会话id，用于自动增强上下文

    @NotEmpty
    private List<ChatCompletionMessage> messages;
    @NotNull
    private ChatOptions chatOptions;

    private Boolean stream;

    public boolean isStream() {
        if (stream == null) {
            return true;
        }
        return stream;
    }
}
