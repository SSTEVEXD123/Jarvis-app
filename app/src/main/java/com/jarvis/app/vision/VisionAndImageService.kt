package com.jarvis.app.vision

import com.jarvis.app.system.AppStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class VisionAndImageService(private val storage: AppStorage) {
    suspend fun generateImage(prompt: String): File = withContext(Dispatchers.IO) {
        storage.ensure()
        val output = File(storage.images, "img_${System.currentTimeMillis()}.txt")
        output.writeText("Stable Diffusion prompt: $prompt")
        output
    }

    suspend fun describeImage(imagePath: String): String = withContext(Dispatchers.IO) {
        "Moondream 2 análisis simulado de: $imagePath"
    }
}
