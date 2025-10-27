package com.example.feature_live_activity_app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class DeliveryLiveActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val progress = intent.getIntExtra("progress", 0)
        val minutes = intent.getIntExtra("minutesToDelivery", 30)
        val isFinished = intent.getBooleanExtra("isFinished", false)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DeliveryLiveActivityContent(
                        currentProgress = progress,
                        minutesToDelivery = minutes,
                        isFinished = isFinished
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryLiveActivityContent(
    currentProgress: Int,
    minutesToDelivery: Int,
    isFinished: Boolean
) {
    if (isFinished) {
        DeliveryArrivedComposable()
    } else {
        DeliveryNotificationComposable(
            progress = currentProgress,
            minutes = minutesToDelivery
        )
    }
}