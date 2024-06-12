package com.dicoding.asclepius.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MAXIMAL_SIZE = 1000000 //1 MB
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(Date())

fun toBitmap(context: Context, image: Uri): Bitmap {
    val contentResolver = context.contentResolver

    val inputStream = contentResolver.openInputStream(image)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    inputStream?.close()
    return bitmap
}

fun createCustomTempFile(context: Context): File {
    val filesDir = context.externalCacheDir
    return File.createTempFile(
        timeStamp,
        ".jpg",
        filesDir
    )
}
