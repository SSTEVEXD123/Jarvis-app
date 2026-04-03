package com.jarvis.app.system

import android.app.ActivityManager
import android.content.Context
import android.os.StatFs

data class SystemSnapshot(val ramUsedMb: Long, val ramTotalMb: Long, val storageUsedMb: Long, val storageTotalMb: Long)

class SystemMonitor(private val context: Context) {
    fun snapshot(): SystemSnapshot {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memoryInfo)

        val stat = StatFs(context.filesDir.path)
        val total = stat.totalBytes / (1024 * 1024)
        val free = stat.availableBytes / (1024 * 1024)
        val used = total - free

        val totalRam = memoryInfo.totalMem / (1024 * 1024)
        val availRam = memoryInfo.availMem / (1024 * 1024)

        return SystemSnapshot(totalRam - availRam, totalRam, used, total)
    }
}
