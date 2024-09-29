package com.krishnajeena.persona.model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class BooksViewModel : ViewModel() {


    val pdfList = mutableStateListOf<File>()

    fun savePdfToAppDirectory(context: Context, pdfUri: Uri) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(pdfUri)

        val pdfDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PersonaPdfs")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val fileName = getFileName(contentResolver, pdfUri)
        val pdfFile = File(pdfDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(pdfFile).use { output ->

                copyFile(input, output)

            }
        }


    }

    fun removePdfFromAppDirectory(context: Context, pdfUri: Uri) {
        val contentResolver = context.contentResolver
        contentResolver.delete(pdfUri, null, null)
        // Alternatively, manually remove from the file system if managing local files
    }


    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String {
        var name = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    // Helper function to copy the file from input stream to output stream
    private fun copyFile(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
    }

    fun loadBooks(context: Context){
        val pdfDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PersonaPdfs")
        if(pdfDir.exists()){
            pdfDir.listFiles()?.forEach {
                files ->
                pdfList.add(files)
            }
        }
    }

}
