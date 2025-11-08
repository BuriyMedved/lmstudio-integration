package org.buriy.medved.frontend

import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.router.HasDynamicTitle
import org.buriy.medved.frontend.chat.ChatView
import org.vaadin.lineawesome.LineAwesomeIcon

class MainLayout : AbstractMainLayout(), HasDynamicTitle {
    companion object{
        private const val TITLE = "Новости бизнеса"
        private const val CHAT_LABEL = "Чат"
    }

    override fun createMenuItems(): Array<Tab> {
        return arrayOf(
            createTab(LineAwesomeIcon.SNAPCHAT.create(), CHAT_LABEL, ChatView::class.java),
        )
    }

    override fun getPageTitle(): String {
        return TITLE
    }
}