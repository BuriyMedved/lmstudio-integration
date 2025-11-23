package org.buriy.medved.backend.service

import org.buriy.medved.backend.dto.DocumentDto
import org.buriy.medved.backend.entity.DocumentEntity
import org.buriy.medved.backend.mapper.DocumentMapper
import org.buriy.medved.backend.repository.DocumentRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DocumentService (
    private val documentRepository: DocumentRepository,
    private val mapper: DocumentMapper,
) {
    fun findDocumentByName(name: String): DocumentDto? {
        val entity = documentRepository.findByName(name)
        return entity?.let { mapper.toDto(it) }
    }

    fun findDocumentId(id: UUID): DocumentDto? {
        val entity = documentRepository.findById(id)
        if (entity.isPresent) {
            return mapper.toDto(entity.get())
        }
        else return null
    }

    fun save(documentDto: DocumentDto): DocumentEntity {
        val entity = mapper.toEntity(documentDto)
        return documentRepository.save(entity)
    }

    fun findAll(): List<DocumentDto>{
        val entities = documentRepository.findAll()
        return entities.map { mapper.toDto(it) }
    }

    fun deleteById(id: UUID){
        documentRepository.deleteById(id)
    }
}