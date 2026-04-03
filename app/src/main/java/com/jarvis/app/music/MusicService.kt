package com.jarvis.app.music

import com.jarvis.app.data.JarvisDao
import com.jarvis.app.data.MusicTrackEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private data class RateState(var count: Int = 0, var windowStart: Long = System.currentTimeMillis())

class MusicService(private val dao: JarvisDao) {
    private val client = HttpClient(CIO)
    private val rateMutex = Mutex()
    private val rateState = RateState()

    suspend fun searchSong(query: String): Result<MusicTrackEntity> {
        return runCatching {
            enforceRateLimit()
            val searchText: String = client.get("https://invidious.nerdvpn.de/api/v1/search") {
                parameter("q", query)
                parameter("type", "video")
            }.body()
            val first = Json.parseToJsonElement(searchText).jsonArray.first().jsonObject
            val id = first["videoId"]!!.jsonPrimitive.content
            val title = first["title"]!!.jsonPrimitive.content
            val artist = first["author"]?.jsonPrimitive?.content ?: "Desconocido"

            val videoText: String = client.get("https://invidious.nerdvpn.de/api/v1/videos/$id").body()
            val root = Json.parseToJsonElement(videoText).jsonObject
            val adaptive = root["adaptiveFormats"]!!.jsonArray
            val audioUrl = adaptive.firstOrNull {
                it.jsonObject["type"]?.jsonPrimitive?.content?.contains("audio") == true
            }?.jsonObject?.get("url")?.jsonPrimitive?.content
                ?: "https://www.youtube.com/watch?v=$id"

            MusicTrackEntity(videoId = id, title = title, artist = artist, streamUrl = audioUrl).also { dao.insertTrack(it) }
        }
    }

    private suspend fun enforceRateLimit() {
        rateMutex.withLock {
            val now = System.currentTimeMillis()
            if (now - rateState.windowStart > 60_000) {
                rateState.windowStart = now
                rateState.count = 0
            }
            if (rateState.count >= 20) error("Límite de API alcanzado. Espera un minuto.")
            rateState.count++
        }
    }
}
