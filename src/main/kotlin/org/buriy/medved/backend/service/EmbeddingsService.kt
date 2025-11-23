package org.buriy.medved.backend.service

import org.springframework.ai.embedding.EmbeddingModel
import org.buriy.medved.backend.dto.DocumentDto
import org.buriy.medved.backend.dto.DocumentSimilarity
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EmbeddingsService (
    private val embeddingModel: EmbeddingModel,
    private val similarityCalculator: SimilarityCalculator
){
    fun embed(query: String): FloatArray {
        val embeddingsArray = embeddingModel.embed(query)
        return embeddingsArray
    }

    fun updateSimilarity(doc: DocumentDto, compareWith: Map<UUID, DocumentSimilarity>){
        return similarityCalculator.calculateSimilarity(
            doc.embeddings,
            compareWith,
        )
    }
}