package org.buriy.medved.backend.dto

import org.buriy.medved.backend.annotation.NoArg
import java.util.UUID

@NoArg
open class BaseDto (open val id: UUID){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseDto) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}