package com.example.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

data class SavedMediaInfo(
  val filePath: String,
  val fileName: String,
  val sizeBytes: Long
)

object ImageStorageHelper {

  fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return saveMediaFile(context, uri, "photo_${System.currentTimeMillis()}.jpg")?.filePath
  }

  fun saveMediaFile(
    context: Context,
    uri: Uri,
    suggestedName: String? = null
  ): SavedMediaInfo? {
    return try {
      val mediaDir = File(context.filesDir, "family_media")
      if (!mediaDir.exists()) {
        mediaDir.mkdirs()
      }

      var extractedName = suggestedName
      if (extractedName.isNullOrBlank()) {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
          val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
          if (nameIndex != -1 && cursor.moveToFirst()) {
            extractedName = cursor.getString(nameIndex)
          }
        }
      }

      val cleanName = (extractedName ?: "media_${System.currentTimeMillis()}")
        .replace("[^a-zA-Z0-9._-]".toRegex(), "_")
      val finalFileName = "${System.currentTimeMillis()}_$cleanName"
      val destinationFile = File(mediaDir, finalFileName)

      val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
      val outputStream = FileOutputStream(destinationFile)

      inputStream?.use { input ->
        outputStream.use { output ->
          input.copyTo(output)
        }
      }

      SavedMediaInfo(
        filePath = destinationFile.absolutePath,
        fileName = extractedName ?: finalFileName,
        sizeBytes = destinationFile.length()
      )
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun deleteImage(path: String?): Boolean {
    if (path.isNullOrEmpty()) return false
    return try {
      val file = File(path)
      if (file.exists()) {
        file.delete()
      } else {
        false
      }
    } catch (e: Exception) {
      false
    }
  }

  fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return when {
      mb >= 1.0 -> String.format("%.1f MB", mb)
      kb >= 1.0 -> String.format("%.0f KB", kb)
      else -> "$bytes B"
    }
  }
}
