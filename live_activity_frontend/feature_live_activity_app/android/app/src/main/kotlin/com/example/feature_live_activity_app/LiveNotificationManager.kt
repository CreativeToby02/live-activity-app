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
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.example.feature_live_activity_app.R

@RequiresApi(Build.VERSION_CODES.O)
class LiveNotificationManager(private val context: Context)  {
    private val remoteViews = RemoteViews("com.example.feature_live_activity_app", R.layout.live_notification)
    private val  channelWithHighPriority = "channelWithHighPriority"
    private val  channelWithDefaultPriority = "channelWithDefaultPriority"
    private val  notificationId = 100
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
       createNotificationChannel(channelWithDefaultPriority)
        createNotificationChannel(channelWithHighPriority, importanceHigh = true)
    }

    private fun createNotificationChannel(channelName : String, importanceHigh : Boolean = false) {
        val importance = if (importanceHigh) IMPORTANCE_HIGH else IMPORTANCE_DEFAULT
        val existingChannel =  (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).getNotificationChannel(channelName)
         if(existingChannel == null){
            val channel =
                NotificationChannel(channelName, "App Delivery Notification", importance).apply {
                    setSound(null, null)
                    vibrationPattern = longArrayOf(0L)
                }

            notificationManager.createNotificationChannel(channel)
        }
    }
    
   private fun onFirstNotification( minutesToDelivery: Int ) : Notification{
        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
        return  Notification.Builder(context, channelWithHighPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Live Notification - Delivery out for shipment")
            .setContentIntent(pendingIntent)
            .setWhen(3000)
            .setContentText("Your delivery comes in $minutesToDelivery $minuteString")
            .setCustomBigContentView(remoteViews)
            .build()
    }

    private fun onGoingNotification(minutesToDelivery: Int ) : Notification{
        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
        return  Notification.Builder(context, channelWithDefaultPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setOngoing(true)
            .setContentTitle("Live Notification - Delivery on the way")
            .setContentIntent(pendingIntent)
            .setContentText("Your delivery comes in $minutesToDelivery $minuteString")
            .setCustomBigContentView(remoteViews)
            .build()
    }

    private fun onFinishNotification() : Notification{
        return Notification.Builder(context,channelWithHighPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setContentTitle("Live Notification - Arrives")
            .setContentText("Your delivery arrive")
            .setCustomBigContentView(remoteViews)
            .build()
    }

    fun showNotification(currentProgress: Int, minutesToDelivery: Int)  {
        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
        val notification = onFirstNotification(minutesToDelivery)
        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery $minuteString")
        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
        remoteViews.setProgressBar(R.id.progress,100,currentProgress,false)
        notificationManager.notify(notificationId, notification)
    }

    fun updateNotification(currentProgress: Int,minutesToDelivery: Int) {
        val notification =  onGoingNotification(minutesToDelivery)
        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery $minuteString")
        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
        remoteViews.setProgressBar(R.id.progress,100,currentProgress,false)
        notificationManager.notify(notificationId, notification)
    }

    fun finishDeliveryNotification() {
        val notification = onFinishNotification()
        remoteViews.setTextViewText(R.id.delivery_message, "Your delivery Arrive")
        remoteViews.setImageViewResource(R.id.image,R.drawable.delivery_arrive)
        remoteViews.setViewVisibility(R.id.progress, View.GONE)
        remoteViews.setViewVisibility(R.id.progress_text, View.GONE)
        remoteViews.setViewVisibility(R.id.minutes_to_delivery, View.GONE)
        remoteViews.setTextViewText(R.id.delivery_subtitle,"Enjoy your delivery :)")
        notificationManager.notify(notificationId, notification)
    }

    fun endNotification() {
      val notificationManager =   context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(channelWithHighPriority)
        notificationManager.deleteNotificationChannel(channelWithDefaultPriority)
        remoteViews.setTextViewText(R.id.delivery_message, "Delivering in ")
        remoteViews.setTextViewText(R.id.delivery_subtitle,"Your delivery is coming")
        remoteViews.setViewVisibility(R.id.progress, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.progress_text, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.minutes_to_delivery, View.VISIBLE)
        remoteViews.setImageViewResource(R.id.image,R.drawable.delivery1)
    }
}