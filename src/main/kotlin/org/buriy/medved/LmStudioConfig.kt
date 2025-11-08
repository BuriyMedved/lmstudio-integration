package org.buriy.medved

import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


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
        return OpenAiApi.builder().baseUrl(baseUrl).apiKey(apiKey).build()
    }

    @Bean
    fun chatModel(openAiApi: OpenAiApi): OpenAiChatModel {
        val options = OpenAiChatOptions.builder()
            .model(modelName)
            .temperature(0.7)
            .build()
        return OpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(options).build()
    }
}