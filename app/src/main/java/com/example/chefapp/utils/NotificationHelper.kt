package com.example.chefapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.chefapp.MainActivity

class NotificationHelper(private val context: Context) {
    private val CHANNEL_ID = "chef_connect_alerts"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alertas de Cocina",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones sobre recetas y errores de sincronización"
            enableVibration(true)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    suspend fun triggerNotification(
        title: String,
        message: String,
        mealId: String? = null,
        imageUrl: String? = null,
        isError: Boolean = false
    ) {
        val intent = if (mealId != null) {
            Intent(
                Intent.ACTION_VIEW,
                "chefapp://mealDetail/$mealId".toUri(),
                context,
                MainActivity::class.java
            )
        } else {
            Intent(context, MainActivity::class.java)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(if (isError) android.R.drawable.stat_notify_error else android.R.drawable.ic_menu_myplaces)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(if (isError) Color.RED else Color.GREEN)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Si hay una URL de imagen, intentamos descargarla para el BigPictureStyle
        if (!imageUrl.isNullOrEmpty()) {
            val bitmap = downloadBitmap(imageUrl)
            if (bitmap != null) {
                builder.setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .setBigContentTitle(title)
                        .setSummaryText(message)
                )
                builder.setLargeIcon(bitmap)
            }
        }

        try {
            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
        } catch (e: SecurityException) {
            // Manejar falta de permisos si es necesario
        }
    }

    private suspend fun downloadBitmap(url: String): Bitmap? {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false) // Necesario para transformaciones de bitmap
            .build()
        
        return try {
            val result = loader.execute(request)
            if (result is SuccessResult) {
                (result.drawable as? BitmapDrawable)?.bitmap
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
