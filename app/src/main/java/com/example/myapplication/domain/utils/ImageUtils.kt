package com.example.myapplication.domain.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

object ImageUtils {

    // Converter imagem em WebP para ser guardada em string na bd
    fun processImageAsWebP(uri: Uri, context: Context): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Converte e verifica tamanho
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, byteArrayOutputStream) // Converte em WebP com qualidade a 80% para diminuir o tamanho
        val webPByteArray = byteArrayOutputStream.toByteArray()
        val imageSizeKB = webPByteArray.size / 1024 // Tamanho em KB para verificar se é >= 200 KB

        return if (imageSizeKB <= 200) BitmapFactory.decodeByteArray(webPByteArray, 0, webPByteArray.size) else null
    }

    // Converte um bitmap WebP para uma string em Base64
    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Guarda a imagem a partir de uma string em base64
    fun saveImageFromBase64(base64String: String, filePath: String) {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val file = File(filePath)
        file.outputStream().use { it.write(decodedBytes) }
    }

    // Converte string em Base64 para bitmap
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}