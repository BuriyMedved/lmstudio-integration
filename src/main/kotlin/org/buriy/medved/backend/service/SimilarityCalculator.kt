package org.buriy.medved.backend.service

import org.buriy.medved.backend.dto.DocumentSimilarity
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.Float
import kotlin.FloatArray
import kotlin.math.pow
import kotlin.math.sqrt


@Service
class SimilarityCalculator {
    private fun calculateCosineSimilarity(vectorA: FloatArray, vectorB: FloatArray): Float {
        var dotProduct = 0.0f
        var normA = 0.0f
        var normB = 0.0f

        for (i in vectorA.indices) {
            dotProduct += vectorA[i] * vectorB[i]
            normA += vectorA[i].toDouble().pow(2.0).toFloat()
            normB += vectorB[i].toDouble().pow(2.0).toFloat()
        }

        return (dotProduct / (sqrt(normA.toDouble()) * sqrt(normB.toDouble()))).toFloat()
    }

    fun calculateSimilarity(
        queryEmbedding: FloatArray,
        compareWith: Map<UUID, DocumentSimilarity>
    ){
        for (documentSimilarity in compareWith.values) {
            val embeddings = documentSimilarity.documentDto.embeddings
            val similarity = calculateCosineSimilarity(queryEmbedding, embeddings)
            documentSimilarity.similarity = similarity
        }
    }
}