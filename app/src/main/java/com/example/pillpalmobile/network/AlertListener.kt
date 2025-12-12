package com.example.pillpalmobile.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*

object AlertListener {

    private const val CHANNEL_ID = "pillpal_alerts"
    private var job: Job? = null

    fun start(context: Context, deviceId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(context)
        }

        // Avoid starting multiple loops
        job?.cancel()

        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                try {
                    val data = RetrofitClient.deviceApi.getAlertStatus(deviceId)

                    if (data.should_alert == true) {
                        showNotification(context)
                    }

                } catch (_: Exception) {}

                delay(5000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "PillPal Medication Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun showNotification(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = context.checkSelfPermission(
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!granted) {
                return
            }
        }

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Medication Alert")
            .setContentText("It's time to take your medication.")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(1001, notif)
    }

}
