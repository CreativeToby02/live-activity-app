package com.example.feature_live_activity_app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.util.DisplayMetrics
import android.view.View.MeasureSpec
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.drawToBitmap
import com.example.feature_live_activity_app.R

class LiveNotificationManager(private val context: Context)  {
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

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelWithDefaultPriority)
            createNotificationChannel(channelWithHighPriority, importanceHigh = true)
        }
    }

    private fun createNotificationChannel(channelName: String, importanceHigh: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = if (importanceHigh) IMPORTANCE_HIGH else IMPORTANCE_DEFAULT
            val existingChannel = notificationManager.getNotificationChannel(channelName)
            if (existingChannel == null) {
                val channel = NotificationChannel(channelName, "App Delivery Notification", importance).apply {
                    setSound(null, null)
                    vibrationPattern = longArrayOf(0L)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun buildBitmap(progress: Int, minutesToDelivery: Int?, finished: Boolean): Bitmap {
        val dm: DisplayMetrics = context.resources.displayMetrics
        val widthPx = (dm.widthPixels.coerceAtMost(1080))
        val heightPx = (dm.density * 120).toInt().coerceAtLeast(200) // ~120dp
        // Use safe XML rendering offscreen to avoid Compose window attachment issues
        return renderXmlLayoutToBitmap(widthPx, heightPx, progress, minutesToDelivery, finished)
    }

    private fun baseBuilder(channelId: String, title: String, text: String): NotificationCompat.Builder =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

    fun showNotification(currentProgress: Int, minutesToDelivery: Int) {
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
        val bmp = buildBitmap(currentProgress, minutesToDelivery, finished = false)
        val builder = baseBuilder(
            channelWithHighPriority,
            "Live Notification - Delivery out for shipment",
            "Your delivery comes in $minutesToDelivery $minuteString"
        )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bmp))

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun updateNotification(currentProgress: Int, minutesToDelivery: Int) {
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
        val bmp = buildBitmap(currentProgress, minutesToDelivery, finished = false)
        val builder = baseBuilder(
            channelWithDefaultPriority,
            "Live Notification - Delivery on the way",
            "Your delivery comes in $minutesToDelivery $minuteString"
        )
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bmp))

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun finishDeliveryNotification() {
        val bmp = buildBitmap(progress = 100, minutesToDelivery = null, finished = true)
        val builder = baseBuilder(
            channelWithHighPriority,
            "Live Notification - Arrives",
            "Your delivery arrive"
        )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bmp))

        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    fun endNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(channelWithHighPriority)
            notificationManager.deleteNotificationChannel(channelWithDefaultPriority)
        }
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    private fun renderComposableToBitmap(width: Int, height: Int, content: @Composable () -> Unit): Bitmap {
        val composeView = ComposeView(context).apply {
            setContent { content() }
            layoutParams = android.view.ViewGroup.LayoutParams(width, height)
            measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
            layout(0, 0, width, height)
        }
        return composeView.drawToBitmap()
    }

    private fun renderXmlLayoutToBitmap(
        width: Int,
        height: Int,
        progress: Int,
        minutesToDelivery: Int?,
        finished: Boolean
    ): Bitmap {
        val root: View = LayoutInflater.from(context).inflate(R.layout.live_notification, null, false)
        val minuteString = minutesToDelivery?.let { if (it > 1) "minutes" else "minute" } ?: "minutes"

        val image = root.findViewById<ImageView>(R.id.image)
        val progressBar = root.findViewById<ProgressBar>(R.id.progress)
        val progressText = root.findViewById<TextView>(R.id.progress_text)
        val minutesText = root.findViewById<TextView>(R.id.minutes_to_delivery)
        val title = root.findViewById<TextView>(R.id.delivery_message)
        val subtitle = root.findViewById<TextView>(R.id.delivery_subtitle)

        if (finished) {
            title.text = "Your delivery Arrive"
            subtitle.text = "Enjoy your delivery :)"
            image.setImageResource(R.drawable.delivery_arrive)
            progressBar.visibility = View.GONE
            progressText.visibility = View.GONE
            minutesText.visibility = View.GONE
        } else {
            title.text = "Delivering in "
            subtitle.text = "Your delivery is coming"
            image.setImageResource(R.drawable.delivery1)
            progressBar.visibility = View.VISIBLE
            progressText.visibility = View.VISIBLE
            minutesText.visibility = View.VISIBLE

            progressBar.max = 100
            progressBar.progress = progress.coerceIn(0, 100)
            progressText.text = "${progress.coerceIn(0, 100)}%"
            minutesToDelivery?.let { minutesText.text = "$it $minuteString" }
        }

        root.layoutParams = android.view.ViewGroup.LayoutParams(width, height)
        val wSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val hSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        root.measure(wSpec, hSpec)
        root.layout(0, 0, width, height)

        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bmp)
        root.draw(canvas)
        return bmp
    }
}