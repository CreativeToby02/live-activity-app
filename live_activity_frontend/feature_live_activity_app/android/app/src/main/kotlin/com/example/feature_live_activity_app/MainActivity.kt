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
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, flutterChannel).setMethodCallHandler { call, result ->
            when (call.method) {
                "startNotifications" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val args = call.arguments<Map<String, Any>>()
                        val progress = args?.get("progress") as? Int
                        val minutes = args?.get("minutesToDelivery") as? Int

                        if (progress != null && minutes != null) {
                            LiveNotificationManager(context).showNotification(progress, minutes)
                        }
                    }
                    result.success("Notification displayed")
                }
                "updateNotifications" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val args = call.arguments<Map<String, Any>>()
                        val progress = args?.get("progress") as? Int
                        val minutes = args?.get("minutesToDelivery") as? Int
                        if (progress != null && minutes != null) {
                            LiveNotificationManager(context)
                                .updateNotification(currentProgress = progress, minutesToDelivery = minutes)
                            result.success("Notification updated")
                        } else {
                            result.error("INVALID_ARGUMENTS", "Progress and minutes are required", null)
                        }
                    }
                }
                "finishDeliveryNotification" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LiveNotificationManager(context).finishDeliveryNotification()
                    }
                    result.success("Notification delivered")
                }
                "endNotifications" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LiveNotificationManager(context).endNotification()
                    }
                    result.success("Notification cancelled")
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, permissions, 200)
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LiveNotificationManager(context).endNotification()
        }
    }
}