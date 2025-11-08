package org.buriy.medved.backend.service

import org.buriy.medved.backend.dto.Message
import org.slf4j.LoggerFactory
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


@Service
class AIService(val chatModel: OpenAiChatModel) {
    private val logger = LoggerFactory.getLogger(javaClass)
    val modelName: String = chatModel.defaultOptions.model ?: "ChatBot"

    private val history: ConcurrentMap<String, Queue<Message>> = ConcurrentHashMap()

    fun chat(message: String?): String? {
        try {
            return chatModel.call(message)
        } catch (e: Exception) {
            throw RuntimeException("Error calling AI model: " + e.message, e)
        }
    }

    fun summarizeText(text: String): String? {
        val prompt = "Please provide a concise summary of the following text:\n\n" + text
        return chatModel.call(prompt)
    }

    fun getHistory(chaId: String): Queue<Message> {
        return history[chaId] ?: LinkedList()
    }

    fun addHistory(chaId: String, message: Message) {
        history.compute(chaId) { s: String?, strings: Queue<Message>? ->
            val queue: Queue<Message> = strings ?: LinkedList()
            queue.add(message)
            queue
        }
    }
}