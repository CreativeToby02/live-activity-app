package com.example.feature_live_activity_app
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.doOnLayout

class ComposeRenderActivity : ComponentActivity() {

    companion object {
        private var contentToRender: (@Composable () -> Unit)? = null
        private var onBitmapReady: ((Bitmap) -> Unit)? = null

        fun renderToBitmap(
            context: Context,
            width: Int,
            height: Int,
            content: @Composable () -> Unit,
            onBitmap: (Bitmap) -> Unit
        ) {
            contentToRender = content
            onBitmapReady = onBitmap
            val intent = Intent(context, ComposeRenderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val composeView = ComposeView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setContent {
                contentToRender?.invoke()
            }
        }

        setContentView(composeView)

        composeView.doOnLayout {
            val bitmap = Bitmap.createBitmap(composeView.width, composeView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            composeView.draw(canvas)

            onBitmapReady?.invoke(bitmap)

            // Clean up to avoid leaks
            contentToRender = null
            onBitmapReady = null

            finish()
        }
    }
}
