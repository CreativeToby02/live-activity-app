package com.example.feature_live_activity_app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        arrayOf()
    }
    private val flutterChannel = "androidInteractiveNotifications"
    
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, flutterChannel).setMethodCallHandler {
                call, result ->
            if (call.method == "startNotifications") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    val args = call.arguments<Map<String, Any>>()
                    val progress = args?.get("progress") as? Int
                    val minutes = args?.get("minutesToDelivery") as? Int

                    if( progress != null && minutes != null){
                        LiveActivityManager(context).startLiveActivity(progress,minutes)
                    }
                }
                result.success("Live Activity started")
            }
            else if (call.method == "updateNotifications") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    val args = call.arguments<Map<String, Any>>()
                    val progress = args?.get("progress") as? Int
                    val minutes = args?.get("minutesToDelivery") as? Int
                    if(progress != null && minutes != null){
                        LiveActivityManager(context)
                            .updateLiveActivity(currentProgress =  progress, minutesToDelivery = minutes)
                    }
                }
            }
            else if (call.method == "finishDeliveryNotification") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    LiveActivityManager(context)
                        .finishLiveActivity()
                }
                result.success("Live Activity finished")
            }
            else if (call.method == "endNotifications") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    LiveActivityManager(context)
                        .endLiveActivity()
                }
                result.success("Live Activity ended")
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, permissions, 200)
        }

        // Check if app was opened from notification
        val fromNotification = intent.getBooleanExtra("from_notification", false)
        if (fromNotification) {
            val progress = intent.getIntExtra("progress", 0)
            val minutes = intent.getIntExtra("minutesToDelivery", 0)

            // Send notification tap info to Flutter
            flutterEngine?.dartExecutor?.binaryMessenger?.let { messenger ->
                MethodChannel(messenger, flutterChannel).invokeMethod("notificationTapped", mapOf(
                    "progress" to progress,
                    "minutesToDelivery" to minutes
                ))
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            LiveActivityManager(context).endLiveActivity()
        }
    }
}