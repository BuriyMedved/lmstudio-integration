package org.buriy.medved.backend.repository

import org.buriy.medved.backend.entity.DocumentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DocumentRepository: JpaRepository<DocumentEntity, UUID>, JpaSpecificationExecutor<DocumentEntity> {
    fun findByName(name: String): DocumentEntity?
}