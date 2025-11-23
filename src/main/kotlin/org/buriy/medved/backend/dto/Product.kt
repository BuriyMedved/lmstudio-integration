package org.buriy.medved.backend.dto

import org.buriy.medved.backend.annotation.NoArg

@NoArg
class Product (
    val id : Int,
    val name: String,
    val description: String
)