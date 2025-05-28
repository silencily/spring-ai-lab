package org.silencer.ai.demo.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatCompletionResponse {
    private String id;
    private List<ChatCompletionChoice> choices;
    private RateLimit rateLimit;
    private Usage usage;

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RateLimit {
        private Long requestsLimit;
        private Long requestsRemaining;
        private Duration requestsReset;
        private Long tokensLimit;
        private Long tokensRemaining;
        private Duration tokensReset;
    }

    @Data
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}
