package org.silencer.ai.demo.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.silencer.ai.core.LlmConfig;
import org.silencer.ai.core.LlmProvider;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatOptions {
    @Tolerate
    public ChatOptions() {
    }

    @NotNull
    private String provider;
    @NotNull
    private String baseUrl;
    @NotNull
    private String apiKey;
    @NotNull
    private String model;
    private String proxyHostname;
    private Integer proxyPort;
    private Double temperature;

    public LlmConfig buildLlmConfig() {
        return LlmConfig.builder()
                .provider(LlmProvider.valueOf(provider))
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .model(model)
                .proxyHostname(proxyHostname)
                .proxyPort(proxyPort)
                .temperature(temperature)
                .build();
    }
}
