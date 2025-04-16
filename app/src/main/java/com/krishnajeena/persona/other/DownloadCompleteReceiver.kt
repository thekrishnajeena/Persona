package com.krishnajeena.persona.other

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.io.File

object DownloadCompleteReceiver : BroadcastReceiver() {
    var downloadId: Long = -1
    var expectedFileName: String = ""
    var expectedMimeType: String = ""

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id != downloadId) return

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)

            if (cursor != null && cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    val uriString = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                    if (uriString != null) {
                        val filePath = Uri.parse(uriString).path
                        if (!filePath.isNullOrEmpty()) {
                            val currentFile = File(filePath)
                            if (currentFile.extension == "bin") {
                                val newFile = File(currentFile.parent, expectedFileName)
                                val renamed = currentFile.renameTo(newFile)
                                if (renamed) {
                                    Toast.makeText(context, "Saved as ${newFile.name}", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to rename file", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
                }
            }

            cursor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error handling download: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            // Always unregister after receiving
            try {
                context.unregisterReceiver(this)
            } catch (e: IllegalArgumentException) {
                // Already unregistered or not registered
            }
        }
    }
}
