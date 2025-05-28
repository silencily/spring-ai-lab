package org.silencer.ai.core;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
@Builder
public class LlmConfig {
    @Tolerate
    public LlmConfig() {
    }

    private LlmProvider provider;
    private String baseUrl;
    private String apiKey;
    private String model;
    private Double temperature;
    private String proxyHostname;
    private Integer proxyPort;
}
