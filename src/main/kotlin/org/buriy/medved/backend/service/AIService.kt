package org.buriy.medved.backend.service

import org.buriy.medved.backend.dto.Message
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.content.Media
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


@Service
class AIService(val chatModel: OpenAiChatModel) {
    private val logger = LoggerFactory.getLogger(javaClass)
    val modelName: String = chatModel.defaultOptions.model ?: "ChatBot"

    private val history: ConcurrentMap<String, Queue<Message>> = ConcurrentHashMap()
    private val lastFile: ConcurrentMap<String, Path> = ConcurrentHashMap()

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

    fun chatWithImage(text: String, path: Path): String?{
        val base64 = buildDataUrl(encodeImageToBase64(path), MimeTypeUtils.IMAGE_JPEG_VALUE)

        val userMessage = UserMessage.builder()
            .text(text)
            .media(
                listOf(
                    Media.builder()
                        .mimeType(MimeTypeUtils.IMAGE_JPEG)
//                        .data(path.toUri())
                        .data(base64)
                        .build()
                )
            )
            .build()

        val response = chatModel
            .call(
                Prompt(
                    listOf(userMessage)
                )
            )

//        return chatModel.call( userMessage)
        return response.result.output.text
    }

    fun encodeImageToBase64(path: Path): String {
        val bytes = Files.readAllBytes(path)
        // Use Java 8 Base64 encoder
        return Base64.getEncoder().encodeToString(bytes)
    }

    // Example of how the data URL format required by some APIs looks:
    fun buildDataUrl(base64Image: String?, mimeType: String?): String {
        return String.format("data:%s;base64,%s", mimeType, base64Image)
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
    fun saveLastFile(chatId: String, file: Path) {
        lastFile[chatId] = file
    }

    fun getLastFile(chatId: String): Path? {
        return lastFile[chatId]
    }

    fun deleteLastFile(chatId: String): Path?{
        return lastFile.remove(chatId)
    }
}