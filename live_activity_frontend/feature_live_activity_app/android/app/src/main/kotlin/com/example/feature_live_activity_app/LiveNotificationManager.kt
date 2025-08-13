////package com.example.feature_live_activity_app
////
////import android.app.Notification
////import android.app.NotificationChannel
////import android.app.NotificationManager
////import android.app.NotificationManager.IMPORTANCE_DEFAULT
////import android.app.NotificationManager.IMPORTANCE_HIGH
////import android.app.PendingIntent
////import android.content.Context
////import android.content.Intent
////import android.os.Build
////import android.view.View
////import android.widget.RemoteViews
////import androidx.annotation.RequiresApi
////import com.example.feature_live_activity_app.R
////
////
////// Imperative approach
////@RequiresApi(Build.VERSION_CODES.O)
////class LiveNotificationManager(private val context: Context)  {
////    private val remoteViews = RemoteViews("com.example.feature_live_activity_app", R.layout.live_notification)
////    private val  channelWithHighPriority = "channelWithHighPriority"
////    private val  channelWithDefaultPriority = "channelWithDefaultPriority"
////    private val  notificationId = 100
////    private val pendingIntent = PendingIntent.getActivity(
////        context,
////        200,
////        Intent(context, MainActivity::class.java).apply {
////            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
////        },
////        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
////    )
////    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////
////    init {
////       createNotificationChannel(channelWithDefaultPriority)
////        createNotificationChannel(channelWithHighPriority, importanceHigh = true)
////    }
////
////    private fun createNotificationChannel(channelName : String, importanceHigh : Boolean = false) {
////        val importance = if (importanceHigh) IMPORTANCE_HIGH else IMPORTANCE_DEFAULT
////        val existingChannel =  (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).getNotificationChannel(channelName)
////         if(existingChannel == null){
////            val channel =
////                NotificationChannel(channelName, "App Delivery Notification", importance).apply {
////                    setSound(null, null)
////                    vibrationPattern = longArrayOf(0L)
////                }
////
////            notificationManager.createNotificationChannel(channel)
////        }
////    }
////
////   private fun onFirstNotification( minutesToDelivery: Int ) : Notification{
////        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
////        return  Notification.Builder(context, channelWithHighPriority)
////            .setSmallIcon(R.drawable.notification_icon)
////            .setContentTitle("Live Notification - Delivery out for shipment")
////            .setContentIntent(pendingIntent)
////            .setWhen(3000)
////            .setContentText("Your delivery comes in $minutesToDelivery $minuteString")
////            .setCustomBigContentView(remoteViews)
////            .build()
////    }
////
////    private fun onGoingNotification(minutesToDelivery: Int ) : Notification{
////        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
////        return  Notification.Builder(context, channelWithDefaultPriority)
////            .setSmallIcon(R.drawable.notification_icon)
////            .setOngoing(true)
////            .setContentTitle("Live Notification - Delivery on the way")
////            .setContentIntent(pendingIntent)
////            .setContentText("Your delivery comes in $minutesToDelivery $minuteString")
////            .setCustomBigContentView(remoteViews)
////            .build()
////    }
////
////    private fun onFinishNotification() : Notification{
////        return Notification.Builder(context,channelWithHighPriority)
////            .setSmallIcon(R.drawable.notification_icon)
////            .setContentIntent(pendingIntent)
////            .setAutoCancel(true)
////            .setContentTitle("Live Notification - Arrives")
////            .setContentText("Your delivery arrive")
////            .setCustomBigContentView(remoteViews)
////            .build()
////    }
////
////    fun showNotification(currentProgress: Int, minutesToDelivery: Int)  {
////        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
////        val notification = onFirstNotification(minutesToDelivery)
////        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery $minuteString")
////        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
////        remoteViews.setProgressBar(R.id.progress,100,currentProgress,false)
////        notificationManager.notify(notificationId, notification)
////    }
////
////    fun updateNotification(currentProgress: Int,minutesToDelivery: Int) {
////        val notification =  onGoingNotification(minutesToDelivery)
////        val minuteString = if  (minutesToDelivery > 1 ) "minutes" else "minute"
////        remoteViews.setTextViewText(R.id.minutes_to_delivery, "$minutesToDelivery $minuteString")
////        remoteViews.setTextViewText(R.id.progress_text, "$currentProgress%")
////        remoteViews.setProgressBar(R.id.progress,100,currentProgress,false)
////
////        notificationManager.notify(notificationId, notification)
////    }
////
////    fun finishDeliveryNotification() {
////        val notification = onFinishNotification()
////        remoteViews.setTextViewText(R.id.delivery_message, "Your delivery Arrive")
////        remoteViews.setImageViewResource(R.id.image,R.drawable.delivery_arrive)
////        remoteViews.setViewVisibility(R.id.progress, View.GONE)
////        remoteViews.setViewVisibility(R.id.progress_text, View.GONE)
////        remoteViews.setViewVisibility(R.id.minutes_to_delivery, View.GONE)
////        remoteViews.setTextViewText(R.id.delivery_subtitle,"Enjoy your delivery :)")
////        notificationManager.notify(notificationId, notification)
////    }
////
////    fun endNotification() {
////      val notificationManager =   context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////        notificationManager.deleteNotificationChannel(channelWithHighPriority)
////        notificationManager.deleteNotificationChannel(channelWithDefaultPriority)
////        remoteViews.setTextViewText(R.id.delivery_message, "Delivering in ")
////        remoteViews.setTextViewText(R.id.delivery_subtitle,"Your delivery is coming")
////        remoteViews.setViewVisibility(R.id.progress, View.VISIBLE)
////        remoteViews.setViewVisibility(R.id.progress_text, View.VISIBLE)
////        remoteViews.setViewVisibility(R.id.minutes_to_delivery, View.VISIBLE)
////        remoteViews.setImageViewResource(R.id.image,R.drawable.delivery1)
////    }
////}
////
//
//
//// declarative approach
//
//// LiveNotificationManager
//
//package com.example.feature_live_activity_app
//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.os.Build
//import android.view.View
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.material3.LinearProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.ComposeView
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.app.NotificationCompat
//
///**
// * Manages live notifications for delivery status using Compose for UI content.
// * Requires API 26 (Android O) for Notification Channels.
// */
//@RequiresApi(Build.VERSION_CODES.O)
//class LiveNotificationManager(private val context: Context) {
//
//    // Notification channel IDs
//    private val channelWithHighPriority = "channelWithHighPriority"
//    private val channelWithDefaultPriority = "channelWithDefaultPriority"
//
//    // Unique ID for the notification
//    private val notificationId = 100
//
//    // PendingIntent to open MainActivity when the notification is tapped
//    private val pendingIntent = PendingIntent.getActivity(
//        context,
//        200,
//        Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT // Bring existing activity to front
//        },
//        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Required for Android S+
//    )
//
//    // System NotificationManager service
//    private val notificationManager =
//        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//    init {
//        // Initialize notification channels when the manager is created
//        createNotificationChannel(channelWithDefaultPriority)
//        createNotificationChannel(channelWithHighPriority, importanceHigh = true)
//    }
//
//    /**
//     * Creates a notification channel. Channels are required for notifications on Android O (API 26) and above.
//     * @param channelName The ID of the notification channel.
//     * @param importanceHigh If true, sets the channel importance to HIGH, otherwise DEFAULT.
//     */
//    private fun createNotificationChannel(channelName: String, importanceHigh: Boolean = false) {
//        val importance =
//            if (importanceHigh) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
//
//        // Check if the channel already exists to avoid recreating it
//        val existingChannel = notificationManager.getNotificationChannel(channelName)
//        if (existingChannel == null) {
//            val channel =
//                NotificationChannel(channelName, "App Delivery Notification", importance).apply {
//                    // Customize channel behavior (e.g., sound, vibration)
//                    setSound(null, null) // No sound
//                    vibrationPattern = longArrayOf(0L) // No vibration
//                }
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    /**
//     * Creates a Notification object using a Composable for its content.
//     * The Composable content is rendered into a Bitmap, which is then used by NotificationCompat.BigPictureStyle.
//     * @param channelId The ID of the notification channel to use.
//     * @param title The title of the notification.
//     * @param contentText The main text content of the notification (shown in collapsed state).
//     * @param isOngoing If true, the notification cannot be dismissed by the user.
//     * @param autoCancel If true, the notification is automatically dismissed when the user taps it.
//     * @param notificationContent A Composable lambda that defines the custom layout of the notification.
//     * @return A configured Notification object.
//     */
//    private fun createNotification(
//        channelId: String,
//        title: String,
//        contentText: String,
//        isOngoing: Boolean = false,
//        autoCancel: Boolean = false,
//        notificationContent: @Composable () -> Unit // Composable content for the notification
//    ): Notification {
//        // Create a ComposeView programmatically to host the Composable content
//        val notificationLayout = ComposeView(context).apply {
//            // Set the Composable content, wrapped in MaterialTheme for proper styling
//            setContent {
//                MaterialTheme {
//                    notificationContent()
//                }
//            }
//        }
//
//        // Convert the Composable content rendered in ComposeView to a Bitmap
//        // This bitmap will be used as the "big picture" for the notification
//        val bitmap = notificationLayout.toBitmap(
//            notificationLayout.width, // Use a reasonable default width or calculate dynamically
//            notificationLayout.height // Use a reasonable default height or calculate dynamically
//        )
//
//        // Build the notification using NotificationCompat.Builder for broader compatibility
//        return NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.drawable.notification_icon) // Small icon required
//            .setContentTitle(title)
//            .setContentText(contentText)
//            .setContentIntent(pendingIntent) // Tap action for the notification
//            .setOngoing(isOngoing) // Set if the notification is ongoing
//            .setAutoCancel(autoCancel) // Set if the notification should auto-cancel on tap
//            // Use BigPictureStyle to display the rendered bitmap as the notification's expanded content
//            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
//            .build()
//    }
//
//    /**
//     * Displays the initial live delivery notification.
//     * @param currentProgress The current progress percentage (0-100).
//     * @param minutesToDelivery The estimated minutes remaining for delivery.
//     */
//    fun showNotification(currentProgress: Int, minutesToDelivery: Int) {
//        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
//        val title = "Live Notification - Delivery out for shipment"
//        val contentText = "Your delivery comes in $minutesToDelivery $minuteString"
//
//        val notification = createNotification(
//            channelWithHighPriority,
//            title,
//            contentText,
//            isOngoing = false // Initial notification might not be ongoing
//        ) {
//            // Pass data to the Composable for rendering the notification UI
//            DeliveryNotificationContent(
//                currentProgress = currentProgress,
//                minutesToDelivery = minutesToDelivery,
//                isFinished = false
//            )
//        }
//        notificationManager.notify(notificationId, notification)
//    }
//
//    /**
//     * Updates an existing live delivery notification.
//     * @param currentProgress The current progress percentage (0-100).
//     * @param minutesToDelivery The estimated minutes remaining for delivery.
//     */
//    fun updateNotification(currentProgress: Int, minutesToDelivery: Int) {
//        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
//        val title = "Live Notification - Delivery on the way"
//        val contentText = "Your delivery comes in $minutesToDelivery $minuteString"
//
//        val notification = createNotification(
//            channelWithDefaultPriority, // Can use default priority for ongoing updates
//            title,
//            contentText,
//            isOngoing = true // Ongoing notification for active delivery
//        ) {
//            // Pass updated data to the Composable
//            DeliveryNotificationContent(
//                currentProgress = currentProgress,
//                minutesToDelivery = minutesToDelivery,
//                isFinished = false
//            )
//        }
//        notificationManager.notify(notificationId, notification)
//    }
//
//    /**
//     * Displays the final notification when the delivery has arrived.
//     */
//    fun finishDeliveryNotification() {
//        val title = "Live Notification - Arrives"
//        val contentText = "Your delivery arrived"
//
//        val notification = createNotification(
//            channelWithHighPriority, // High priority for arrival notification
//            title,
//            contentText,
//            autoCancel = true // Auto-cancel when tapped
//        ) {
//            // Render the finished state of the notification UI
//            DeliveryNotificationContent(
//                currentProgress = 100, // Delivery is complete
//                minutesToDelivery = 0,
//                isFinished = true
//            )
//        }
//        notificationManager.notify(notificationId, notification)
//    }
//
//    /**
//     * Cancels the currently displayed notification.
//     */
//    fun endNotification() {
//        notificationManager.cancel(notificationId)
//        // Optionally, you could delete notification channels here if they are no longer needed
//        // notificationManager.deleteNotificationChannel(channelWithHighPriority)
//        // notificationManager.deleteNotificationChannel(channelWithDefaultPriority)
//    }
//
//    /**
//     * Extension function to convert a ComposeView's content into a Bitmap.
//     * This is necessary because notifications primarily work with Bitmaps or RemoteViews for custom layouts.
//     * @param width The desired width of the bitmap.
//     * @param height The desired height of the bitmap.
//     * @return A Bitmap representation of the ComposeView's content.
//     */
//    private fun ComposeView.toBitmap(width: Int, height: Int): Bitmap {
//        // Measure the ComposeView to determine its actual content size
//        // Using AT_MOST for height allows the content to dictate its height up to the specified max.
//        measure(
//            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
//            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)
//        )
//        // Layout the ComposeView with its measured dimensions
//        layout(0, 0, width, measuredHeight)
//
//        // Create a new bitmap with the measured dimensions
//        val bitmap = Bitmap.createBitmap(width, measuredHeight, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//
//        // Draw the background of the ComposeView onto the canvas.
//        // If no background is set, draw a default white background.
//        val backgroundDrawable = background
//        if (backgroundDrawable != null) {
//            backgroundDrawable.draw(canvas)
//        } else {
//            canvas.drawColor(android.graphics.Color.WHITE)
//        }
//        // Draw the ComposeView's content onto the canvas
//        draw(canvas)
//        return bitmap
//    }
//}
//
///**
// * A Composable function that defines the custom UI for the delivery notification.
// * @param currentProgress The current delivery progress percentage.
// * @param minutesToDelivery The estimated minutes remaining for delivery.
// * @param isFinished A boolean indicating if the delivery is complete.
// * @param modifier Modifier for custom layout adjustments.
// */
//@Composable
//fun DeliveryNotificationContent(
//    currentProgress: Int,
//    minutesToDelivery: Int,
//    isFinished: Boolean,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth() // Fill the available width
//            .padding(16.dp) // Add padding around the content
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            // Display an image based on delivery status
//            Image(
//                painter = painterResource(id = if (isFinished) R.drawable.delivery_arrive else R.drawable.delivery1),
//                contentDescription = "Delivery Icon",
//                modifier = Modifier.size(48.dp) // Set icon size
//            )
//            Spacer(modifier = Modifier.width(16.dp)) // Space between image and text
//            Column {
//                // Main message text
//                Text(
//                    text = if (isFinished) "Your delivery Arrived!" else "Delivering in:",
//                    style = MaterialTheme.typography.titleMedium, // Use MaterialTheme typography
//                    color = Color.Black // Set text color
//                )
//                Spacer(modifier = Modifier.height(4.dp)) // Small space below main message
//                if (isFinished) {
//                    // Subtitle for finished delivery
//                    Text(
//                        text = "Enjoy your delivery :)",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.DarkGray
//                    )
//                } else {
//                    // Display minutes to delivery for ongoing status
//                    val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
//                    Text(
//                        text = "$minutesToDelivery $minuteString",
//                        style = MaterialTheme.typography.headlineSmall,
//                        color = MaterialTheme.colorScheme.primary // Use primary color from theme
//                    )
//                }
//            }
//        }
//
//        // Show progress bar and text only if delivery is not finished
//        if (!isFinished) {
//            Spacer(modifier = Modifier.height(8.dp)) // Space above progress bar
//            LinearProgressIndicator(
//                progress = currentProgress / 100f, // Convert percentage to float (0.0-1.0)
//                modifier = Modifier.fillMaxWidth().height(8.dp), // Fill width, set height
//                color = MaterialTheme.colorScheme.primary, // Progress bar color
//                trackColor = Color.LightGray // Background track color
//            )
//            Spacer(modifier = Modifier.height(4.dp)) // Space below progress bar
//            Text(
//                text = "$currentProgress%",
//                fontSize = 12.sp,
//                modifier = Modifier.align(Alignment.End), // Align percentage text to the end
//                color = Color.Gray
//            )
//        }
//    }
//}


// EmptyLifecycleOwner.kt (Can be a top-level file or nested inside LiveNotificationManager)
package com.example.feature_live_activity_app

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View.MeasureSpec
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat


/**
 * A minimal LifecycleOwner implementation for use with ComposeView when it's not
 * attached to a real Android View hierarchy. It allows Compose to perform its
 * initial composition and rendering to a Bitmap.
 */
class EmptyLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)

    init {
        // Start in CREATED state to allow initialization
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    /**
     * Moves the lifecycle to RESUMED, which is typically when Composables start
     * active composition and drawing.
     */
    fun resume() {
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    /**
     * Moves the lifecycle to DESTROYED to clean up resources after bitmap rendering.
     */
    fun destroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}




/**
 * Manages live notifications for delivery status using Compose for UI content.
 * Requires API 26 (Android O) for Notification Channels.
 */
@RequiresApi(Build.VERSION_CODES.O)
class LiveNotificationManager(private val context: Context) {

    // Notification channel IDs
    private val channelWithHighPriority = "channelWithHighPriority"
    private val channelWithDefaultPriority = "channelWithDefaultPriority"

    // Unique ID for the notification
    private val notificationId = 100

    // PendingIntent to open MainActivity when the notification is tapped
    private val pendingIntent = PendingIntent.getActivity(
        context,
        200,
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT // Bring existing activity to front
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Required for Android S+
    )

    // System NotificationManager service
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        // Initialize notification channels when the manager is created
        createNotificationChannel(channelWithDefaultPriority)
        createNotificationChannel(channelWithHighPriority, importanceHigh = true)
    }

    /**
     * Creates a notification channel. Channels are required for notifications on Android O (API 26) and above.
     * @param channelName The ID of the notification channel.
     * @param importanceHigh If true, sets the channel importance to HIGH, otherwise DEFAULT.
     */
    private fun createNotificationChannel(channelName: String, importanceHigh: Boolean = false) {
        val importance =
            if (importanceHigh) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT

        // Check if the channel already exists to avoid recreating it
        val existingChannel = notificationManager.getNotificationChannel(channelName)
        if (existingChannel == null) {
            val channel =
                NotificationChannel(channelName, "App Delivery Notification", importance).apply {
                    // Customize channel behavior (e.g., sound, vibration)
                    setSound(null, null) // No sound
                    vibrationPattern = longArrayOf(0L) // No vibration
                }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Creates a Notification object using a Composable for its content.
     * The Composable content is rendered into a Bitmap, which is then used by NotificationCompat.BigPictureStyle.
     * @param channelId The ID of the notification channel to use.
     * @param title The title of the notification.
     * @param contentText The main text content of the notification (shown in collapsed state).
     * @param isOngoing If true, the notification cannot be dismissed by the user.
     * @param autoCancel If true, the notification is automatically dismissed when the user taps it.
     * @param notificationContent A Composable lambda that defines the custom layout of the notification.
     * @return A configured Notification object.
     */
    private fun createNotification(
        channelId: String,
        title: String,
        contentText: String,
        isOngoing: Boolean = false,
        autoCancel: Boolean = false,
        notificationContent: @Composable () -> Unit // Composable content for the notification
    ): Notification {
        // Create a ComposeView programmatically to host the Composable content
        // IMPORTANT: Use application context.
        val notificationLayout = ComposeView(context.applicationContext).apply {
            // Set the Composable content, wrapped in MaterialTheme for proper styling
            setContent {
                MaterialTheme {
                    notificationContent()
                }
            }
        }

        // Convert the Composable content rendered in ComposeView to a Bitmap
        val bitmap = notificationLayout.toBitmap(
            // Provide a reasonable fixed width for the notification content.
            // Notifications typically have a maximum content width.
            // 360dp is a common width for notification content. Convert to pixels.
            dpToPx(360).toInt(),
            // Provide a maximum height for the bitmap. AT_MOST will respect content height.
            // 200dp is a generous max height for notification custom views.
            dpToPx(200).toInt()
        )

        // Build the notification using NotificationCompat.Builder for broader compatibility
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_icon) // Small icon required
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(pendingIntent) // Tap action for the notification
            .setOngoing(isOngoing) // Set if the notification is ongoing
            .setAutoCancel(autoCancel) // Set if the notification should auto-cancel on tap
            // Use BigPictureStyle to display the rendered bitmap as the notification's expanded content
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .build()
    }

    /**
     * Converts dp to pixels.
     */
    private fun dpToPx(dp: Int): Float {
        return dp * context.resources.displayMetrics.density
    }

    /**
     * Displays the initial live delivery notification.
     * @param currentProgress The current progress percentage (0-100).
     * @param minutesToDelivery The estimated minutes remaining for delivery.
     */
    fun showNotification(currentProgress: Int, minutesToDelivery: Int) {
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
        val title = "Live Notification - Delivery out for shipment"
        val contentText = "Your delivery comes in $minutesToDelivery $minuteString"

        val notification = createNotification(
            channelWithHighPriority,
            title,
            contentText,
            isOngoing = false // Initial notification might not be ongoing
        ) {
            // Pass data to the Composable for rendering the notification UI
            DeliveryNotificationContent(
                currentProgress = currentProgress,
                minutesToDelivery = minutesToDelivery,
                isFinished = false
            )
        }
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Updates an existing live delivery notification.
     * @param currentProgress The current progress percentage (0-100).
     * @param minutesToDelivery The estimated minutes remaining for delivery.
     */
    fun updateNotification(currentProgress: Int, minutesToDelivery: Int) {
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
        val title = "Live Notification - Delivery on the way"
        val contentText = "Your delivery comes in $minutesToDelivery $minuteString"

        val notification = createNotification(
            channelWithDefaultPriority, // Can use default priority for ongoing updates
            title,
            contentText,
            isOngoing = true // Ongoing notification for active delivery
        ) {
            // Pass updated data to the Composable
            DeliveryNotificationContent(
                currentProgress = currentProgress,
                minutesToDelivery = minutesToDelivery,
                isFinished = false
            )
        }
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Displays the final notification when the delivery has arrived.
     */
    fun finishDeliveryNotification() {
        val title = "Live Notification - Arrives"
        val contentText = "Your delivery arrived"

        val notification = createNotification(
            channelWithHighPriority, // High priority for arrival notification
            title,
            contentText,
            autoCancel = true // Auto-cancel when tapped
        ) {
            // Render the finished state of the notification UI
            DeliveryNotificationContent(
                currentProgress = 100, // Delivery is complete
                minutesToDelivery = 0,
                isFinished = true
            )
        }
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Cancels the currently displayed notification.
     */
    fun endNotification() {
        notificationManager.cancel(notificationId)
        // Optionally, you could delete notification channels here if they are no longer needed
        // notificationManager.deleteNotificationChannel(channelWithHighPriority)
        // notificationManager.deleteNotificationChannel(channelWithDefaultPriority)
    }

    /**
     * Extension function to convert a ComposeView's content into a Bitmap.
     * This is necessary because notifications primarily work with Bitmaps or RemoteViews for custom layouts.
     * @param viewWidthPx The desired width of the bitmap in pixels.
     * @param viewHeightPx The desired height of the bitmap in pixels (used as AT_MOST).
     * @return A Bitmap representation of the ComposeView's content.
     */
    private fun ComposeView.toBitmap(viewWidthPx: Int, viewHeightPx: Int): Bitmap {
        // Create a minimal LifecycleOwner for this detached ComposeView
        val lifecycleOwner = EmptyLifecycleOwner()
        // Attach the lifecycle owner to the ComposeView's view tree
//        ViewTreeLifecycleOwner.set(this, lifecycleOwner)
        lifecycleOwner.resume() // Move lifecycle to RESUMED state for composition to occur

        // Measure the ComposeView to determine its actual content size
        // We need to provide MeasureSpecs explicitly when not attached to a window.
        measure(
            MeasureSpec.makeMeasureSpec(viewWidthPx, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeightPx, MeasureSpec.AT_MOST)
        )
        // Layout the ComposeView with its measured dimensions
        layout(0, 0, viewWidthPx, measuredHeight) // Use measuredHeight for the actual height

        // Create a new bitmap with the measured dimensions
        val bitmap = Bitmap.createBitmap(viewWidthPx, measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw the background of the ComposeView onto the canvas.
        val backgroundDrawable = background
        if (backgroundDrawable != null) {
            backgroundDrawable.draw(canvas)
        } else {
            // Draw a default white background if none is set, to ensure content is visible
            canvas.drawColor(android.graphics.Color.WHITE)
        }
        // Draw the ComposeView's content onto the canvas
        draw(canvas)

        // Clean up the lifecycle owner after drawing the bitmap
        lifecycleOwner.destroy()
//        ViewTreeLifecycleOwner.set(this, null) // Clear the lifecycle owner
        return bitmap
    }
}

/**
 * A Composable function that defines the custom UI for the delivery notification.
 * @param currentProgress The current delivery progress percentage.
 * @param minutesToDelivery The estimated minutes remaining for delivery.
 * @param isFinished A boolean indicating if the delivery is complete.
 * @param modifier Modifier for custom layout adjustments.
 */
@Composable
fun DeliveryNotificationContent(
    currentProgress: Int,
    minutesToDelivery: Int,
    isFinished: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth() // Fill the available width
            .padding(16.dp) // Add padding around the content
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Display an image based on delivery status
            Image(
                painter = painterResource(id = if (isFinished) R.drawable.delivery_arrive else R.drawable.delivery1),
                contentDescription = "Delivery Icon",
                modifier = Modifier.size(48.dp) // Set icon size
            )
            Spacer(modifier = Modifier.width(16.dp)) // Space between image and text
            Column {
                // Main message text
                Text(
                    text = if (isFinished) "Your delivery Arrived!" else "Delivering in:",
                    style = MaterialTheme.typography.titleMedium, // Use MaterialTheme typography
                    color = Color.Black // Set text color
                )
                Spacer(modifier = Modifier.height(4.dp)) // Small space below main message
                if (isFinished) {
                    // Subtitle for finished delivery
                    Text(
                        text = "Enjoy your delivery :)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                } else {
                    // Display minutes to delivery for ongoing status
                    val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
                    Text(
                        text = "$minutesToDelivery $minuteString",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary // Use primary color from theme
                    )
                }
            }
        }

        // Show progress bar and text only if delivery is not finished
        if (!isFinished) {
            Spacer(modifier = Modifier.height(8.dp)) // Space above progress bar
            LinearProgressIndicator(
                progress = currentProgress / 100f, // Convert percentage to float (0.0-1.0)
                modifier = Modifier.fillMaxWidth().height(8.dp), // Fill width, set height
                color = MaterialTheme.colorScheme.primary, // Progress bar color
                trackColor = Color.LightGray // Background track color
            )
            Spacer(modifier = Modifier.height(4.dp)) // Space below progress bar
            Text(
                text = "$currentProgress%",
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End), // Align percentage text to the end
                color = Color.Gray
            )
        }
    }
}

/**
 * Preview Composable for `DeliveryNotificationContent` to see how it looks in Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun PreviewDeliveryNotificationContent() {
    MaterialTheme { // Wrap in MaterialTheme for proper preview rendering
        Column {
            // Preview for ongoing delivery
            DeliveryNotificationContent(currentProgress = 50, minutesToDelivery = 10, isFinished = false)
            Spacer(modifier = Modifier.height(16.dp))
            // Preview for finished delivery
            DeliveryNotificationContent(currentProgress = 100, minutesToDelivery = 0, isFinished = true)
        }
    }
}