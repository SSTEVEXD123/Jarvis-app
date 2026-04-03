package com.jarvis.app.voice

sealed class VoiceCommand {
    data class PlayMusic(val query: String) : VoiceCommand()
    data class GenerateImage(val prompt: String) : VoiceCommand()
    data object Unknown : VoiceCommand()
}

object VoiceCommandParser {
    fun parse(input: String): VoiceCommand {
        val text = input.lowercase()
        return when {
            text.startsWith("reproduce ") -> VoiceCommand.PlayMusic(input.removePrefix("reproduce "))
            text.startsWith("genera imagen ") -> VoiceCommand.GenerateImage(input.removePrefix("genera imagen "))
            else -> VoiceCommand.Unknown
        }
    }
}
