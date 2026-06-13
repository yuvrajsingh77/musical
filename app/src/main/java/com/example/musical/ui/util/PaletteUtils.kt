package com.example.musical.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

object PaletteUtils {
    suspend fun getDominantColor(context: Context, imageUrl: String): Color {
        if (imageUrl.isBlank()) return Color(0xFF1DB954)
        return try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false) // Must be false to read pixels from bitmap
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                val drawable = result.drawable
                val bitmap = (drawable as? BitmapDrawable)?.bitmap
                if (bitmap != null) {
                    val palette = Palette.from(bitmap).generate()
                    Color(palette.getDominantColor(0xFF1DB954.toInt()))
                } else {
                    Color(0xFF1DB954)
                }
            } else {
                Color(0xFF1DB954)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Color(0xFF1DB954)
        }
    }
}
