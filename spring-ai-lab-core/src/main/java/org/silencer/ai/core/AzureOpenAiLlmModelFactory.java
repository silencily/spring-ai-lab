package org.silencer.ai.core;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.util.ClientOptions;
import com.azure.core.util.HttpClientOptions;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.tool.ToolCallingManager;

import java.net.InetSocketAddress;

public class AzureOpenAiLlmModelFactory implements LlmModelFactory {
    private static final String APPLICATION_ID = "spring-ai";
    private ToolCallingManager toolCallingManager;
    private ObservationRegistry observationRegistry;
    private ChatModelObservationConvention observationConvention;

    public AzureOpenAiLlmModelFactory(ToolCallingManager toolCallingManager, ObservationRegistry observationRegistry,
                                      ChatModelObservationConvention observationConvention) {
        this.toolCallingManager = toolCallingManager;
        this.observationRegistry = observationRegistry;
        this.observationConvention = observationConvention;
    }

    @Override
    public LlmProvider provider() {
        return LlmProvider.azureopenai;
    }

    @Override
    public ChatModel createChatModel(LlmConfig config) {
        ClientOptions clientOptions = new ClientOptions().setApplicationId(APPLICATION_ID);
        OpenAIClientBuilder openAIClientBuilder = new OpenAIClientBuilder().endpoint(config.getBaseUrl())
                .credential(new AzureKeyCredential(config.getApiKey()))
                .clientOptions(clientOptions);
        if (StringUtils.isNotBlank(config.getProxyHostname()) && config.getProxyPort() != null) {
            HttpClientOptions httpClientOptions = new HttpClientOptions()
                    .setProxyOptions(new ProxyOptions(ProxyOptions.Type.HTTP,
                            new InetSocketAddress(config.getProxyHostname(), config.getProxyPort())));
            openAIClientBuilder.httpClient(HttpClient.createDefault(httpClientOptions));
        }
        AzureOpenAiChatOptions openAiChatOptions = AzureOpenAiChatOptions.builder()
                .deploymentName(config.getModel())
                .temperature(config.getTemperature())
                .build();
        var chatModel = AzureOpenAiChatModel.builder()
                .openAIClientBuilder(openAIClientBuilder)
                .defaultOptions(openAiChatOptions)
                .toolCallingManager(toolCallingManager)
                .observationRegistry(observationRegistry)
                .build();
        if (observationConvention != null) {
            chatModel.setObservationConvention(observationConvention);
        }
        return chatModel;
    }
}
