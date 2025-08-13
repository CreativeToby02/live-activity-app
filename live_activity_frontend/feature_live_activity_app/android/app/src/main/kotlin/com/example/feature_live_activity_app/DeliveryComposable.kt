// DeliveryComposable.kt
package com.example.feature_live_activity_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeliveryNotificationComposable(progress: Int, minutes: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        Text(text = "Delivery in $minutes minute(s)", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(progress = progress / 100f)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "$progress% completed", fontSize = 12.sp)
    }
}



@Composable
fun DeliveryArrivedComposable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Your delivery has arrived!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Enjoy your package 🎉", fontSize = 14.sp)
    }
}
