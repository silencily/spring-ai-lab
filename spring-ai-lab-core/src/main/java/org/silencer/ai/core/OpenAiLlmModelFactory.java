package org.silencer.ai.core;

import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.HttpHost;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpComponentsClientHttpRequestFactoryBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import javax.annotation.Nullable;

public class OpenAiLlmModelFactory implements LlmModelFactory {
    public static final String DEFAULT_COMPLETIONS_PATH = "/v1/chat/completions";
    public static final String DEFAULT_EMBEDDINGS_PATH = "/v1/embeddings";
    private RestClient.Builder restClientBuilder;
    private WebClient.Builder webClientBuilder;
    private ToolCallingManager toolCallingManager;
    private RetryTemplate retryTemplate;
    private ResponseErrorHandler responseErrorHandler;
    private ObservationRegistry observationRegistry;
    private ChatModelObservationConvention observationConvention;

    public OpenAiLlmModelFactory(RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder, ToolCallingManager toolCallingManager,
                                 RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler, ObservationRegistry observationRegistry,
                                 @Nullable ChatModelObservationConvention observationConvention) {
        this.restClientBuilder = restClientBuilder;
        this.webClientBuilder = webClientBuilder;
        this.toolCallingManager = toolCallingManager;
        this.retryTemplate = retryTemplate;
        this.responseErrorHandler = responseErrorHandler;
        this.observationRegistry = observationRegistry;
        this.observationConvention = observationConvention;
    }

    @Override
    public LlmProvider provider() {
        return LlmProvider.openai;
    }

    @Override
    public ChatModel createChatModel(LlmConfig config) {
        RestClient.Builder restClientBuilder = this.restClientBuilder;
        WebClient.Builder webClientBuilder = this.webClientBuilder;
        if (StringUtils.isNotBlank(config.getProxyHostname()) && config.getProxyPort() != null) {
            HttpComponentsClientHttpRequestFactoryBuilder requestFactoryBuilder = ClientHttpRequestFactoryBuilder.httpComponents()
                    .withHttpClientCustomizer(httpClient -> httpClient.setProxy(new HttpHost(config.getProxyHostname(), config.getProxyPort())));
            restClientBuilder = restClientBuilder.clone().requestFactory(requestFactoryBuilder.build());
            ReactorClientHttpConnector httpConnector = new ReactorClientHttpConnector(HttpClient.create()
                    .proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(config.getProxyHostname())
                            .port(config.getProxyPort())));
            webClientBuilder = webClientBuilder.clone().clientConnector(httpConnector);
        }
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(config.getBaseUrl())
                .apiKey(new SimpleApiKey(config.getApiKey()))
                .completionsPath(DEFAULT_COMPLETIONS_PATH)
                .embeddingsPath(DEFAULT_EMBEDDINGS_PATH)
                .restClientBuilder(restClientBuilder)
                .webClientBuilder(webClientBuilder)
                .responseErrorHandler(responseErrorHandler)
                .build();
        var chatOptions = OpenAiChatOptions.builder()
                .model(config.getModel())
                .temperature(config.getTemperature())
                .build();
        var chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatOptions)
                .toolCallingManager(toolCallingManager)
                .retryTemplate(retryTemplate)
                .observationRegistry(observationRegistry)
                .build();
        if (observationConvention != null) {
            chatModel.setObservationConvention(observationConvention);
        }
        return chatModel;
    }
}
