package com.jarvis.app.models

import com.jarvis.app.system.AppStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

data class AiModel(val id: String, val description: String, val url: String)

class ModelManager(private val storage: AppStorage) {
    val availableModels = listOf(
        AiModel("tinyllama-1.1b", "Modelo pequeño para llama.cpp", "https://huggingface.co/ggml-org/models/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf"),
        AiModel("qwen2.5-1.5b", "Qwen para tareas complejas", "https://huggingface.co/Qwen/Qwen2.5-1.5B-Instruct-GGUF/resolve/main/qwen2.5-1.5b-instruct-q4_k_m.gguf"),
        AiModel("moondream2", "Modelo de visión Moondream 2", "https://huggingface.co/vikhyatk/moondream2/resolve/main/model-q4_k_m.gguf"),
        AiModel("stable-diffusion", "Stable Diffusion base", "https://huggingface.co/stabilityai/sd-turbo/resolve/main/model_index.json")
    )

    suspend fun install(model: AiModel): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            storage.ensure()
            val output = java.io.File(storage.models, "${model.id}.bin")
            URL(model.url).openStream().use { input ->
                output.outputStream().use { out -> input.copyTo(out) }
            }
            output.absolutePath
        }
    }

    fun optimizedSpanishPrompt(userMessage: String): String {
        return """
            Eres Jarvis, asistente personal técnico y profesional en español.
            Responde de forma extensa, estructurada y práctica.
            Incluye pasos accionables y evita respuestas ambiguas.
            Consulta del usuario: $userMessage
        """.trimIndent()
    }
}
