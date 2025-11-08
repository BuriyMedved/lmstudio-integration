package org.buriy.medved.backend.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.UUID

@Service
class FileUploadService {
    @Value("\${upload.dir}")
    private lateinit var uploadDir: String

    fun saveFile(data: ByteArray): Path {
        val uuid = UUID.randomUUID()
        val path = Path.of(uploadDir.trim(), "$uuid.tmp")
        Files.write(path, data, StandardOpenOption.CREATE)

        return path
    }

    fun deleteIfExists(path: Path) {
       Files.deleteIfExists(path)
    }
}