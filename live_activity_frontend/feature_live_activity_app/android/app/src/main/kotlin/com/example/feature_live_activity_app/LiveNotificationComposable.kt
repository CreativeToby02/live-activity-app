package com.example.feature_live_activity_app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LiveNotificationContent(
    progress: Int,
    minutesToDelivery: Int?,
    finished: Boolean,
) {
    val context = LocalContext.current
    val minuteString = minutesToDelivery?.let { if (it > 1) "minutes" else "minute" }
    val bg = colorResource(id = R.color.notification_background)

    Surface(color = Color.Transparent) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bg)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = if (finished) R.drawable.delivery_arrive else R.drawable.delivery1),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (finished) "Your delivery Arrive" else "Delivering in ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.primary_text)
                )
                Text(
                    text = if (finished) "Enjoy your delivery :)" else "Your delivery is coming",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.secondary_text)
                )

                if (!finished && minutesToDelivery != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Simple custom progress bar
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colorResource(id = R.color.progress_text).copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = (progress.coerceIn(0, 100) / 100f))
                                    .background(colorResource(id = R.color.progress_color))
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${progress.coerceIn(0,100)}%",
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.progress_text)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${minutesToDelivery} ${minuteString}",
                        fontSize = 12.sp,
                        color = colorResource(id = R.color.time_text)
                    )
                }
            }
        }
    }
}
