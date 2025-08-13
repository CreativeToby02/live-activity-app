package com.example.feature_live_activity_app


import android.content.Context


import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.*
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun composeToBitmap(
    context: Context,
    composable: @Composable () -> Unit,
    width: Int,
    height: Int
): Bitmap = withContext(Dispatchers.Main) {
    val deferred = CompletableDeferred<Bitmap>()

    val rootLayout = FrameLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(0, 0)
    }

    val composeView = ComposeView(context).apply {
        layoutParams = ViewGroup.LayoutParams(width, height)
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        setContent { composable() }
    }

    rootLayout.addView(composeView)

    val decorView = (context as? android.app.Activity)?.window?.decorView as? ViewGroup
        ?: error("Context is not an Activity or cannot access window decorView")

    decorView.addView(rootLayout)

    composeView.post {
        try {
            composeView.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            )
            composeView.layout(0, 0, width, height)

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            composeView.draw(canvas)

            decorView.removeView(rootLayout)
            deferred.complete(bitmap)
        } catch (e: Exception) {
            deferred.completeExceptionally(e)
        }
    }

    deferred.await()
}
