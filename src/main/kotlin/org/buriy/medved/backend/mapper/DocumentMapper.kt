package org.buriy.medved.backend.mapper

import org.buriy.medved.backend.dto.DocumentDto
import org.buriy.medved.backend.entity.DocumentEntity
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface DocumentMapper {
    fun toDto(entity: DocumentEntity): DocumentDto
    fun toEntity(dto: DocumentDto): DocumentEntity
}