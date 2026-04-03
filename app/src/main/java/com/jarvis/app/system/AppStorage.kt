package com.jarvis.app.system

import android.content.Context
import java.io.File

class AppStorage(private val context: Context) {
    private val root = File(context.filesDir, "JarvisApp")
    val models = File(root, "models")
    val data = File(root, "data")
    val pdfs = File(root, "pdfs")
    val images = File(root, "images")
    val music = File(root, "music")

    fun ensure() {
        listOf(root, models, data, pdfs, images, music).forEach { it.mkdirs() }
    }

    fun createFile(relativePath: String, content: String): File {
        val f = File(root, relativePath)
        f.parentFile?.mkdirs()
        f.writeText(content)
        return f
    }

    fun readFile(relativePath: String): String = File(root, relativePath).takeIf { it.exists() }?.readText().orEmpty()

    fun updateFile(relativePath: String, content: String): File = createFile(relativePath, content)

    fun deleteFile(relativePath: String): Boolean = File(root, relativePath).delete()
}
