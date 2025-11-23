package org.buriy.medved.frontend

import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.router.HasDynamicTitle
import org.buriy.medved.frontend.chat.ChatView
import org.buriy.medved.frontend.chat.EmbeddingsView
import org.vaadin.lineawesome.LineAwesomeIcon

class MainLayout : AbstractMainLayout(), HasDynamicTitle {
    companion object{
        private const val TITLE = "ИИ помощник"
    }

    override fun createMenuItems(): Array<Tab> {
        return arrayOf(
            createTab(LineAwesomeIcon.SNAPCHAT.create(), ChatView.TITLE, ChatView::class.java),
            createTab(LineAwesomeIcon.CALCULATOR_SOLID.create(), EmbeddingsView.TITLE, EmbeddingsView::class.java),
        )
    }

    override fun getPageTitle(): String {
        return TITLE
    }
}