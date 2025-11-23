package org.buriy.medved.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import org.springframework.data.domain.Persistable
import java.util.UUID

@MappedSuperclass
class BaseEntity : Persistable<UUID> {
    @Id
    @Column(name = "z_id", unique = true, nullable = false, length = 36)
    private var id: UUID = UUID.randomUUID()

    @Version
    @Column(name = "z_version", nullable = false)
    var version: Long = -1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun getId(): UUID {
        return id
    }

    fun setId(id: UUID) {
        this.id = id
    }

    override fun isNew(): Boolean {
        return version < 0
    }
}