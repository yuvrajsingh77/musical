package com.example.musical.ui.util

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

object PaletteUtils {
    private val colorCache = mutableMapOf<String, Color>()
    private var sharedImageLoader: ImageLoader? = null

    suspend fun getDominantColor(context: Context, imageUrl: String): Color {
        if (imageUrl.isBlank()) return Color(0xFF1DB954)
        colorCache[imageUrl]?.let { return it }

        return try {
            val loader = sharedImageLoader
                ?: ImageLoader(context.applicationContext).also { sharedImageLoader = it }

            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val result = loader.execute(request)
            val color = if (result is SuccessResult) {
                val bitmap = (result.drawable as? BitmapDrawable)?.bitmap
                if (bitmap != null) {
                    val palette = Palette.from(bitmap).generate()
                    Color(palette.getDominantColor(0xFF1DB954.toInt()))
                } else Color(0xFF1DB954)
            } else Color(0xFF1DB954)

            colorCache[imageUrl] = color
            color
        } catch (e: Exception) {
            Color(0xFF1DB954)
        }
    }

    fun clearCache() {
        colorCache.clear()
    }
}
