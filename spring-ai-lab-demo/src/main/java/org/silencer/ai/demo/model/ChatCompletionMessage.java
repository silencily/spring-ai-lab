package org.silencer.ai.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.springframework.ai.chat.messages.MessageType;

@Data
@Builder
public class ChatCompletionMessage {
    @Tolerate
    public ChatCompletionMessage() {
    }

    private MessageType role;
    private String content;
}
