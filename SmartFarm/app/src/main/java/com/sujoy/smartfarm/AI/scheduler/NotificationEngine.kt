package com.sujoy.smartfarm.AI.scheduler

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.sujoy.smartfarm.MainActivity
import com.sujoy.smartfarm.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class NotificationEngine @Inject constructor(

    @ApplicationContext
    private val context: Context

) {

    companion object {

        const val CHANNEL_ID = "today_tasks"

        const val CHANNEL_NAME = "Today's Farming Tasks"

    }

    init {

        createChannel()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {

        val channel = NotificationChannel(

            CHANNEL_ID,

            CHANNEL_NAME,

            NotificationManager.IMPORTANCE_HIGH

        )

        val manager =

            context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(channel)

    }

    fun showTodayTaskNotification(
        title: String,
        message: String
    ) {

        val intent = Intent(
            context,
            MainActivity::class.java
        ).apply {

            putExtra("open_schedule", true)

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = BitmapFactory.decodeResource(
            context.resources,
            R.mipmap.ic_launcher  // your full-color app icon, used as the large icon
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notification)   // flat white silhouette
            .setLargeIcon(largeIcon)                          // full-color icon shown on the right
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setColor(ContextCompat.getColor(context, R.color.notification_accent))
            .setColorized(false) // set true only if you also set a solid background color intentionally
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(context)
            .notify(1001, notification)
    }

}