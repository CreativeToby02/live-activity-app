package com.example.feature_live_activity_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.feature_live_activity_app.R

/**
 * Manages live activities for delivery status using custom notification layouts.
 * Displays rich composable-like UI directly in notifications using RemoteViews.
 */
class LiveActivityManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = "delivery_live_activity"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Delivery Live Activity",
                NotificationManager.IMPORTANCE_HIGH // Maximum allowed importance level
            ).apply {
                description = "Shows delivery progress updates"
                setShowBadge(true)
                enableVibration(true)
                enableLights(true)
                lightColor = android.graphics.Color.BLUE
                vibrationPattern = longArrayOf(0, 250, 250, 250) // Custom vibration pattern
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Starts the live activity for delivery status.
     * @param currentProgress The current progress percentage (0-100).
     * @param minutesToDelivery The estimated minutes remaining for delivery.
     */
    fun startLiveActivity(currentProgress: Int, minutesToDelivery: Int) {
        showCustomNotification(currentProgress, minutesToDelivery)
    }

    private fun showCustomNotification(currentProgress: Int, minutesToDelivery: Int) {
        // Create intent to open the main Flutter app when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Add extras to indicate this came from notification
            putExtra("from_notification", true)
            putExtra("progress", currentProgress)
            putExtra("minutesToDelivery", minutesToDelivery)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create custom notification layout using RemoteViews - this mimics composable UI
        val remoteViews = RemoteViews(context.packageName, R.layout.notification_delivery).apply {
            setTextViewText(R.id.notification_title, "🚚 Delivery in Progress")
            setTextViewText(R.id.progress_text, "$currentProgress% complete")
            setTextViewText(R.id.time_text, "$minutesToDelivery min remaining")
            setProgressBar(R.id.progress_bar, 100, currentProgress, false)
        }

        // Create expanded view for better visibility
        val bigRemoteViews = RemoteViews(context.packageName, R.layout.notification_delivery).apply {
            setTextViewText(R.id.notification_title, "🚚 Delivery in Progress")
            setTextViewText(R.id.progress_text, "$currentProgress% complete")
            setTextViewText(R.id.time_text, "$minutesToDelivery min remaining")
            setProgressBar(R.id.progress_bar, 100, currentProgress, false)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🚚 Delivery Update")
            .setContentText("$minutesToDelivery min • $currentProgress% complete")
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(bigRemoteViews)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .build()

        notificationManager.notify(1001, notification)
    }

    /**
     * Updates the live activity with new delivery status.
     * @param currentProgress The current progress percentage (0-100).
     * @param minutesToDelivery The estimated minutes remaining for delivery.
     */
    fun updateLiveActivity(currentProgress: Int, minutesToDelivery: Int) {
        showCustomNotification(currentProgress, minutesToDelivery)
    }

    /**
     * Ends the live activity.
     */
    fun finishLiveActivity() {
        notificationManager.cancel(1001)
    }

    /**
     * Ends the live activity (alias for finishLiveActivity).
     */
    fun endLiveActivity() {
        finishLiveActivity()
    }
}
