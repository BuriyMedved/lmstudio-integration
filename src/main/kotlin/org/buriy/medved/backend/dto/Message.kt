package org.buriy.medved.backend.dto

import java.time.Instant

data class Message(
    val message: String,
    val assistant: Boolean,
    val time: Instant,
)