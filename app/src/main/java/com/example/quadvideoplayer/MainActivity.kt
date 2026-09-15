package com.example.quadvideoplayer

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.quadvideoplayer.ui.QuadPlayerScreen
import com.example.quadvideoplayer.ui.theme.QuadVideoPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // SMCPKG_SUPPORT>>>Cursor004
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }
        // SMCPKG_SUPPORT<<<Cursor004
        setContent {
            QuadVideoPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QuadPlayerScreen()
                }
            }
        }
    }
}
