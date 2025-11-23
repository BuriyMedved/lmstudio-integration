package org.buriy.medved.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Array
import org.hibernate.annotations.Type

@Entity
@Table(name = "z_document")
data class DocumentEntity(
    @Column(name = "z_text", nullable = false, columnDefinition = "TEXT")
    var text: String,
    @Column(name = "z_name", nullable = false)
    var name: String,

//    @Type(value = CustomFloatArrayType::class)
//    @Column(name = "z_embeddings", nullable = false, columnDefinition = "real[]")
    @Array(length = 4096)
    @Column(name = "z_embeddings", nullable = false)
    var embeddings: FloatArray,
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}