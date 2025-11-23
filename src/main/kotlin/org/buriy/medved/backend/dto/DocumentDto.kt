package org.buriy.medved.backend.dto

import java.util.UUID

data class DocumentDto (
    override val id: UUID,
    val text: String,
    val name: String,
    val embeddings: FloatArray,
): BaseDto(id){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}