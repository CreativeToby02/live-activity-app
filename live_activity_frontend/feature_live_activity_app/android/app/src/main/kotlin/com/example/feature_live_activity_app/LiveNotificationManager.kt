package com.example.feature_live_activity_app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class LiveNotificationManager(private val context: Context) {
    private val TAG = "LiveNotification"
    private val remoteViews = RemoteViews("com.example.feature_live_activity_app", R.layout.live_notification)
    private val channelWithHighPriority = "channelWithHighPriority"
    private val channelWithDefaultPriority = "channelWithDefaultPriority"
    private val notificationId = 100
    
    private val pendingIntent = PendingIntent.getActivity(
        context,
        200,
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    
    private val notificationManager = 
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel(channelWithDefaultPriority)
        createNotificationChannel(channelWithHighPriority, importanceHigh = true)
        Log.d(TAG, "LiveNotificationManager initialized")
    }

    private fun createNotificationChannel(channelName: String, importanceHigh: Boolean = false) {
        val importance = if (importanceHigh) IMPORTANCE_HIGH else IMPORTANCE_DEFAULT
        val existingChannel = notificationManager.getNotificationChannel(channelName)
        
        if (existingChannel == null) {
            val channel = NotificationChannel(
                channelName,
                "App Delivery Notification",
                importance
            ).apply {
                setSound(null, null)
                vibrationPattern = longArrayOf(0L)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Created notification channel: $channelName")
        }
    }
    
    private fun onFirstNotification(minutesToDelivery: Int): Notification {
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
        return Notification.Builder(context, channelWithHighPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("🚚 Delivery Out for Shipment")
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis() + (minutesToDelivery * 60 * 1000L))
            .setShowWhen(true)
            .setContentText("Your delivery comes in $minutesToDelivery $minuteString")
            .setCustomBigContentView(remoteViews)
            .build()
    }

    private fun onGoingNotification(minutesToDelivery: Int): Notification {
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
        return Notification.Builder(context, channelWithDefaultPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentTitle("🚚 Delivery on the Way")
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis() + (minutesToDelivery * 60 * 1000L))
            .setShowWhen(true)
            .setContentText("Your delivery comes in $minutesToDelivery $minuteString")
            .setCustomBigContentView(remoteViews)
            .build()
    }

    private fun onFinishNotification(): Notification {
        return Notification.Builder(context, channelWithHighPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setContentTitle("✅ Delivery Arrived!")
            .setContentText("Your delivery has arrived")
            .setCustomBigContentView(remoteViews)
            .build()
    }
    
/**
 * Draw progress bar with moving icon as a custom bitmap
 * This provides pixel-perfect control over icon positioning
 */
private fun createProgressBitmap(currentProgress: Int): android.graphics.Bitmap {
    val displayMetrics = context.resources.displayMetrics
    val density = displayMetrics.density
    
    // Define dimensions
    val width = (displayMetrics.widthPixels - (128 * density)).toInt() // Account for notification padding
    val height = (34 * density).toInt() // Height for progress bar + icon
    val progressBarHeight = (8 * density).toInt()
    val iconSize = (24 * density).toInt()
    
    // Create bitmap and canvas
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    
    // Paint for progress bar background
    val bgPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#E0E0E0") // Light gray background
        style = android.graphics.Paint.Style.FILL
        isAntiAlias = true
    }
    
    // Paint for progress bar foreground
    val progressPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#81878D") // Green progress
        style = android.graphics.Paint.Style.FILL
        isAntiAlias = true
    }
    
    // Calculate vertical center for progress bar
    val progressBarTop = (height - progressBarHeight) / 2f
    val progressBarBottom = progressBarTop + progressBarHeight
    val cornerRadius = progressBarHeight / 2f
    
    // Draw progress bar background
    val bgRect = android.graphics.RectF(0f, progressBarTop, width.toFloat(), progressBarBottom)
    canvas.drawRoundRect(bgRect, cornerRadius, cornerRadius, bgPaint)
    
    // Draw progress bar foreground
    val clampedProgress = currentProgress.coerceIn(0, 100)
    val progressWidth = (width * clampedProgress / 100f)
    if (progressWidth > 0) {
        val progressRect = android.graphics.RectF(0f, progressBarTop, progressWidth, progressBarBottom)
        canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint)
    }
    
    // Use your actual image file names:
val iconResource = when {
    clampedProgress < 25 -> R.drawable.moving_car  // or truck, delivery_car, etc.
    clampedProgress < 75 -> R.drawable.moving_car  // same image or different
    clampedProgress < 100 -> R.drawable.moving_car  // if you have a "fast" version
    else -> R.drawable.moving_car // or just use same car
}
    
    val iconDrawable = context.getDrawable(iconResource)
    if (iconDrawable != null) {
        // Calculate icon position (centered on progress)
        val iconX = ((width - iconSize) * clampedProgress / 100f).toInt()
        val iconY = (height - iconSize) / 2
        
        iconDrawable.setBounds(iconX, iconY, iconX + iconSize, iconY + iconSize)
        iconDrawable.draw(canvas)
    }
    
    Log.d(TAG, "Created progress bitmap: progress=$clampedProgress%, width=$width, iconX=${(width - iconSize) * clampedProgress / 100f}")
    
    return bitmap
}

/**
 * Update the progress icon by drawing a custom bitmap
 */
private fun updateProgressIcon(currentProgress: Int) {
    try {
        val progressBitmap = createProgressBitmap(currentProgress)
        remoteViews.setImageViewBitmap(R.id.progress_with_icon, progressBitmap)
        remoteViews.setViewVisibility(R.id.progress_with_icon, View.VISIBLE)
    } catch (e: Exception) {
        Log.e(TAG, "Error creating progress bitmap", e)
    }
}

fun showNotification(currentProgress: Int, minutesToDelivery: Int) {
    try {
        Log.d(TAG, "Showing notification: progress=$currentProgress%, minutes=$minutesToDelivery")
        
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
        val notification = onFirstNotification(minutesToDelivery)
        
        remoteViews.setTextViewText(R.id.delivery_message, "Delivering in ")
        remoteViews.setTextViewText(R.id.delivery_subtitle, "Your delivery is on its way")
        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery $minuteString")
        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
        
        // Hide the old progress bar and icon, show bitmap version
        remoteViews.setViewVisibility(R.id.progress, View.GONE)
        remoteViews.setViewVisibility(R.id.progress_icon, View.GONE)
        
        // Update with bitmap
        updateProgressIcon(currentProgress)
        
        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "Notification posted successfully")
    } catch (e: Exception) {
        Log.e(TAG, "Error showing notification", e)
    }
}

fun updateNotification(currentProgress: Int, minutesToDelivery: Int) {
    try {
        Log.d(TAG, "Updating notification: progress=$currentProgress%, minutes=$minutesToDelivery")
        
        val notification = onGoingNotification(minutesToDelivery)
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
        
        remoteViews.setTextViewText(R.id.delivery_message, "Delivering in ")
        remoteViews.setTextViewText(R.id.delivery_subtitle, "Your delivery is on its way")
        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery $minuteString")
        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
        
        // Hide the old progress bar and icon, show bitmap version
        remoteViews.setViewVisibility(R.id.progress, View.GONE)
        remoteViews.setViewVisibility(R.id.progress_icon, View.GONE)
        
        // Update with bitmap
        updateProgressIcon(currentProgress)
        
        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "Notification updated successfully")
    } catch (e: Exception) {
        Log.e(TAG, "Error updating notification", e)
    }
}

fun finishDeliveryNotification() {
    try {
        Log.d(TAG, "Finishing delivery notification")
        
        val notification = onFinishNotification()
        
        remoteViews.setTextViewText(R.id.delivery_message, "Delivery Arrived! 🎉")
        remoteViews.setTextViewText(R.id.delivery_subtitle, "Enjoy your delivery :)")
        remoteViews.setImageViewResource(R.id.image, R.drawable.delivery_arrive)
        
        // Hide all progress elements
        remoteViews.setViewVisibility(R.id.progress, View.GONE)
        remoteViews.setViewVisibility(R.id.progress_text, View.GONE)
        remoteViews.setViewVisibility(R.id.progress_icon, View.GONE)
        remoteViews.setViewVisibility(R.id.progress_with_icon, View.GONE)
        remoteViews.setViewVisibility(R.id.minutes_to_delivery, View.GONE)
        
        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "Finish notification posted successfully")
    } catch (e: Exception) {
        Log.e(TAG, "Error finishing notification", e)
    }
}

    fun endNotification() {
        try {
            Log.d(TAG, "Canceling notification")
            notificationManager.cancel(notificationId)
        } catch (e: Exception) {
            Log.e(TAG, "Error canceling notification", e)
        }
    }
}