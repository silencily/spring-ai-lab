package org.silencer.ai.demo.config;

import com.google.common.collect.Lists;
import io.micrometer.observation.ObservationRegistry;
import org.silencer.ai.core.AzureOpenAiLlmModelFactory;
import org.silencer.ai.core.ChatClientBuilder;
import org.silencer.ai.core.LlmModelFactory;
import org.silencer.ai.core.OpenAiLlmModelFactory;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.autoconfigure.ToolCallingAutoConfiguration;
import org.springframework.ai.retry.autoconfigure.SpringAiRetryAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
@ImportAutoConfiguration(classes = {SpringAiRetryAutoConfiguration.class, RestClientAutoConfiguration.class,
        WebClientAutoConfiguration.class, ToolCallingAutoConfiguration.class})
public class LlmConfiguration {

    @Bean
    public AzureOpenAiLlmModelFactory azureOpenAiLlmModelFactory(ToolCallingManager toolCallingManager,
                                                                 ObjectProvider<ObservationRegistry> observationRegistry,
                                                                 ObjectProvider<ChatModelObservationConvention> observationConvention) {
        return new AzureOpenAiLlmModelFactory(toolCallingManager,
                observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP),
                observationConvention.getIfAvailable(() -> null));
    }

    @Bean
    public OpenAiLlmModelFactory openAiLlmModelFactory(ObjectProvider<RestClient.Builder> restClientBuilderProvider,
                                                       ObjectProvider<WebClient.Builder> webClientBuilderProvider, ToolCallingManager toolCallingManager,
                                                       RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler,
                                                       ObjectProvider<ObservationRegistry> observationRegistry,
                                                       ObjectProvider<ChatModelObservationConvention> observationConvention) {
        return new OpenAiLlmModelFactory(restClientBuilderProvider.getIfAvailable(RestClient::builder),
                webClientBuilderProvider.getIfAvailable(WebClient::builder), toolCallingManager, retryTemplate, responseErrorHandler,
                observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP), observationConvention.getIfAvailable(() -> null));
    }

    @Bean
    public ChatClientBuilder chatClientBuilder(List<LlmModelFactory> llmModelFactories) {
        return new ChatClientBuilder(llmModelFactories, Lists.newArrayList(), Lists.newArrayList());
    }
}
