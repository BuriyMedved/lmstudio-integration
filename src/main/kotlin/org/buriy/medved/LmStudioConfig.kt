package org.buriy.medved

import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.TimeoutRetryPolicy
import org.springframework.retry.support.RetryTemplate
import java.time.Duration


@Configuration
class LmStudioConfig {
    @Value("\${spring.ai.openai.base-url}")
    private val baseUrl: String? = null

    @Value("\${spring.ai.openai.api-key}")
    private val apiKey: String? = null

    @Value("\${spring.ai.openai.chat.options.model}")
    private val modelName: String? = null

    @Bean
    fun openAiApi(): OpenAiApi {
        val builder = OpenAiApi.builder()

//        val client: HttpClient = HttpClient.create()
////            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120000)
//            .responseTimeout(Duration.ofSeconds(1))
//
//        val webClientBuilder = WebClient.builder()
//        webClientBuilder
//            .clientConnector(ReactorClientHttpConnector(client))

//        builder.webClientBuilder(webClientBuilder)

        val openAiApi = builder
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .build()
        return openAiApi
    }


    @Bean
    fun chatModel(openAiApi: OpenAiApi): OpenAiChatModel {
        val options = OpenAiChatOptions.builder()
            .model(modelName)
            .temperature(0.7)
            .build()

        val retryTemplate = RetryTemplate()
        retryTemplate.setBackOffPolicy(FixedBackOffPolicy())

        val retryPolicy = TimeoutRetryPolicy()
        //максимальное время на все попытки
        retryPolicy.timeout = Duration.ofSeconds(66).toMillis()

        retryTemplate.setRetryPolicy(retryPolicy)
        retryTemplate.setThrowLastExceptionOnExhausted(true)

        val modelBuilder = OpenAiChatModel.builder()

        modelBuilder.openAiApi(openAiApi)
                    .defaultOptions(options)
                    .retryTemplate(retryTemplate)

        return modelBuilder.build()
    }
}