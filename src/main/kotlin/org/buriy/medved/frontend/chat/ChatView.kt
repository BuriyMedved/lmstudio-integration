package org.buriy.medved.frontend.chat

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.messages.MessageInput
import com.vaadin.flow.component.messages.MessageInput.SubmitEvent
import com.vaadin.flow.component.messages.MessageList
import com.vaadin.flow.component.messages.MessageListItem
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.theme.lumo.LumoUtility
import org.buriy.medved.backend.dto.Message
import org.buriy.medved.backend.service.AIService
import org.buriy.medved.frontend.MainLayout
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@Route(value = "chat", layout = MainLayout::class)
@CssImport(value = "./styles/components/common.css")
class ChatView(private val aiService: AIService): VerticalLayout(), HasDynamicTitle  {
    companion object {
        private val logger = LoggerFactory.getLogger(ChatView::class.java)
    }

    private val greetingsTitle = "HELLO!"
    private val waitingText = "AI is processing the prompt"
    private val chatId = "1234" // Placeholder chat identifier

    init {
        add(H2(greetingsTitle))

        val commentsList = MessageList()
        add(commentsList)


        val list = MessageList()
        list.isMarkdown = true

        // Live region for screen reader announcements
        val liveRegion = Div()
        liveRegion.element.setAttribute("aria-live", "polite")
        liveRegion.addClassName(LumoUtility.Accessibility.SCREEN_READER_ONLY)

        add(liveRegion)

        val history = aiService.getHistory(chatId)

        val oldChatList = history.stream()
            .map { message: Message -> createItem(message) }
            .toList()
        
        list.setItems( oldChatList )

        val input = MessageInput()
        input.addSubmitListener { event: SubmitEvent ->
            val userInput = event.value

            if(userInput.isBlank()){
                if(logger.isTraceEnabled){
                    logger.trace("User input is blank")
                }
                return@addSubmitListener
            }

            // Add the user message to the list
            list.addItem(createItem(userInput, false))

//            // Add the Assistant message to the list
//            val newAssistantMessage: MessageListItem = createItem("", true)
//            list.addItem(newAssistantMessage)

            // Announce that AI is processing
            liveRegion.text = waitingText

            val response = aiService.chat(userInput)

            if(logger.isTraceEnabled){
                logger.trace("Response is: {}", response)
            }

            if(response == null || response.isBlank()){
                return@addSubmitListener
            }

            liveRegion.text = ""
            // Add the Assistant message to the list
            val newAssistantMessage: MessageListItem = createItem(response, true)
            list.addItem(newAssistantMessage)
        }

        add(list, input)
    }

    private fun createItem(message: Message): MessageListItem {
        val userName = if (message.assistant) "Assistant" else "User"

        val item = MessageListItem(
            message.message,
            message.time,
            userName
        )

        item.setUserColorIndex(if (message.assistant) 2 else 1)
        return item
    }

    private fun createItem(text: String, assistant: Boolean): MessageListItem {
        val userName = if (assistant) "Assistant" else "User"
        val time = Instant.now()

        val item = MessageListItem(
            text,
            time,
            userName
        )
        item.setUserColorIndex(if (assistant) 2 else 1)

        aiService.addHistory(chatId, Message(
            text,
            assistant,
            time,
        ))
        return item
    }

    override fun getPageTitle(): String {
        return "Чат"
    }
}