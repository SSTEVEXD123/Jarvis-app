package com.jarvis.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jarvis.app.data.ChatMessageEntity
import com.jarvis.app.data.JarvisDatabase
import com.jarvis.app.models.ModelManager
import com.jarvis.app.music.MusicService
import com.jarvis.app.network.PublicApiClient
import com.jarvis.app.pdf.PdfService
import com.jarvis.app.system.AppStorage
import com.jarvis.app.system.SystemMonitor
import com.jarvis.app.vision.VisionAndImageService
import com.jarvis.app.voice.VoiceCommand
import com.jarvis.app.voice.VoiceCommandParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiState(
    val input: String = "",
    val latestSystem: String = "",
    val musicStatus: String = "Listo",
    val apiSummary: String = "",
)

class JarvisViewModel(context: Context) : ViewModel() {
    private val storage = AppStorage(context).also { it.ensure() }
    private val dao = JarvisDatabase.get(context, java.io.File(storage.data, "jarvis.db").absolutePath).dao()
    private val modelManager = ModelManager(storage)
    private val musicService = MusicService(dao)
    private val pdfService = PdfService(storage)
    private val apiClient = PublicApiClient()
    private val visionService = VisionAndImageService(storage)
    private val monitor = SystemMonitor(context)

    val messages = dao.observeMessages().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun onInputChanged(value: String) { _state.value = _state.value.copy(input = value) }

    fun sendMessage() = viewModelScope.launch {
        val input = _state.value.input
        if (input.isBlank()) return@launch
        dao.insertMessage(ChatMessageEntity(role = "user", text = input))
        val reply = modelManager.optimizedSpanishPrompt(input)
        dao.insertMessage(ChatMessageEntity(role = "assistant", text = "Respuesta simulada:\n$reply"))
        _state.value = _state.value.copy(input = "")
    }

    fun createPdfFromInput() = viewModelScope.launch {
        val file = pdfService.createPdf("nota", _state.value.input.ifBlank { "Documento Jarvis" })
        dao.insertMessage(ChatMessageEntity(role = "assistant", text = "PDF generado: ${file.name}"))
    }

    fun updateSystem() {
        val s = monitor.snapshot()
        _state.value = _state.value.copy(
            latestSystem = "CPU: monitoreo base | RAM ${s.ramUsedMb}/${s.ramTotalMb} MB | Almacenamiento ${s.storageUsedMb}/${s.storageTotalMb} MB"
        )
    }

    fun callPublicApi(url: String) = viewModelScope.launch {
        val text = apiClient.fetchSummary(url)
        _state.value = _state.value.copy(apiSummary = text)
    }

    fun handleVoiceCommand(command: String) = viewModelScope.launch {
        when (val parsed = VoiceCommandParser.parse(command)) {
            is VoiceCommand.PlayMusic -> searchMusic(parsed.query)
            is VoiceCommand.GenerateImage -> {
                val img = visionService.generateImage(parsed.prompt)
                dao.insertMessage(ChatMessageEntity(role = "assistant", text = "Imagen creada en ${img.name}"))
            }
            VoiceCommand.Unknown -> dao.insertMessage(ChatMessageEntity(role = "assistant", text = "Comando de voz no reconocido."))
        }
    }

    fun searchMusic(query: String) = viewModelScope.launch {
        val result = musicService.searchSong(query)
        _state.value = _state.value.copy(musicStatus = result.fold(
            onSuccess = { "Reproduciendo: ${it.title} - ${it.artist}" },
            onFailure = { "Error música: ${it.message}" }
        ))
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = JarvisViewModel(context) as T
    }
}
