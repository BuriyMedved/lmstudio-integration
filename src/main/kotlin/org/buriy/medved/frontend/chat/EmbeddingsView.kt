package org.buriy.medved.frontend.chat

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.streams.UploadHandler
import com.vaadin.flow.server.streams.UploadMetadata
import org.apache.tika.Tika
import org.buriy.medved.backend.dto.DocumentDto
import org.buriy.medved.backend.dto.DocumentSimilarity
import org.buriy.medved.backend.service.DocumentService
import org.buriy.medved.backend.service.EmbeddingsService
import org.buriy.medved.backend.service.FileUploadService
import org.buriy.medved.frontend.MainLayout
import org.slf4j.LoggerFactory
import java.util.UUID


@Route(value = "embed", layout = MainLayout::class)
@CssImport(value = "./styles/components/common.css")
class EmbeddingsView(
    private val embeddingsService: EmbeddingsService,
    private val uploadService: FileUploadService,
    private val documentService: DocumentService,
): VerticalLayout(), HasDynamicTitle  {
    companion object {
        private val logger = LoggerFactory.getLogger(EmbeddingsView::class.java)
        const val TITLE = "Сравнения"
    }

    private val uploadDocument = "Загрузить документ"
    private val docTitle = "Название"
    private val similarityTitle = "Результат"
    private val emptyDocsTable = "Нет загруженных документов"
    private val closeButtonText = "Закрыть"

    private val documentsGrid = Grid<DocumentDto>()

    private val deleteButtonText = "Удалить"
    private val compareButtonText = "Сравнить"
    private val actionsColumnTitle = "Действия"

    init {
        documentsGrid.emptyStateText = emptyDocsTable

        add(H2(uploadDocument))

        val uploadLayout = HorizontalLayout()

        createDocGrid()
        refreshTable()

        val inMemoryHandler = UploadHandler.inMemory { metadata: UploadMetadata?, data: ByteArray? ->

            val fileName = metadata!!.fileName()
            val mimeType = metadata.contentType()
            val contentLength = metadata.contentLength()
            if(logger.isDebugEnabled){
                logger.debug("Загрузка файла. fileName = $fileName contentLength = $contentLength mime type = $mimeType")
            }

            if (data == null || data.isEmpty()) {
                return@inMemory
            }
            val tmpFilePath = uploadService.saveFile(data)
            val content: String? = Tika().parseToString(tmpFilePath)

            if(content != null && content.isNotBlank()) {
                val embeddings = embeddingsService.embed(content)
                documentService.save(DocumentDto(UUID.randomUUID(), content, fileName, embeddings))
            }

            refreshTable()

        }
        val upload = Upload(inMemoryHandler)
        upload.addAllFinishedListener {
            upload.clearFileList()
        }

        uploadLayout.add(upload)
        add(uploadLayout, documentsGrid)
    }

    private fun createDocGrid() {
        documentsGrid.addColumn(DocumentDto::name).setHeader(docTitle)

        documentsGrid.addComponentColumn { doc ->
            val docId = doc.id

            val buttonsLayout = HorizontalLayout()

            val compareButton = Button(compareButtonText, VaadinIcon.BAR_CHART_H.create()) {
                openCompareDialog(doc)
            }

            val deleteButton = Button(deleteButtonText, VaadinIcon.TRASH.create()) {
                documentService.deleteById(docId)
                refreshTable()
            }

            buttonsLayout.add(compareButton, deleteButton)
            buttonsLayout
        }
        .setHeader(actionsColumnTitle)
            .setAutoWidth(true)
            .setFlexGrow(0)

        documentsGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS)
    }

    private fun refreshTable() {
        val findAll = documentService.findAll()
        documentsGrid.setItems(findAll)
    }

    private fun openCompareDialog(documentDto: DocumentDto) {
        val selectedDocuments = HashMap<UUID, DocumentSimilarity>()

        val dialog = Dialog().apply {
            minWidth = "600px"
        }

        val allExceptThis = documentService.findAll().filter { documentDto.id != it.id }

        val selectDocumentsGrid = Grid<DocumentSimilarity>()

        selectDocumentsGrid.addComponentColumn { doc ->
            val checkbox = Checkbox()
            checkbox.addValueChangeListener { event ->
                if(event.value){
                    selectedDocuments[doc.documentDto.id] = doc
                }
                else{
                    selectedDocuments.remove(doc.documentDto.id)
                }
            }
            checkbox
        }

        selectDocumentsGrid.addColumn({it.documentDto.name} ).setHeader(docTitle)
        selectDocumentsGrid.addColumn(DocumentSimilarity::similarity).setHeader(similarityTitle)

        selectDocumentsGrid.setItems(
            allExceptThis.map { document -> DocumentSimilarity(document, 0.toFloat()) }
        )

        val buttons = HorizontalLayout()

        val choose = Button(compareButtonText, VaadinIcon.CALC.create()) {
            embeddingsService.updateSimilarity(
                documentDto,
                selectedDocuments
            )

            for (similarity in selectedDocuments.values) {
                selectDocumentsGrid.dataProvider.refreshItem(similarity)
            }
        }
        val close = Button(closeButtonText, VaadinIcon.CLOSE.create()){
            dialog.close()
        }

        buttons.add(choose, close)

        dialog.add(selectDocumentsGrid, buttons)
        dialog.open()
    }

    override fun getPageTitle(): String {
        return TITLE
    }
}
