package com.blocklegends.lite

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 100
        params.y = 100

        composeView = ComposeView(this).apply {
            setContent {
                var flyEnabled by remember { mutableStateOf(false) }
                
                Surface(
                    modifier = Modifier
                        .size(65.dp)
                        .clickable { 
                            flyEnabled = !flyEnabled 
                            HackConfig.flyEnabled = flyEnabled
                        },
                    shape = CircleShape,
                    color = if (flyEnabled) Color(0xFF6200EE) else Color(0xFF2D2D2D),
                    tonalElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (flyEnabled) "ON" else "FLY", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

        val viewModelStore = ViewModelStore()
        val lifecycleOwner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry(this).apply {
                currentState = Lifecycle.State.RESUMED
            }
        }
        
        composeView?.let {
            it.setViewTreeLifecycleOwner(lifecycleOwner)
            it.setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
                override val viewModelStore = viewModelStore
            })
            // Fix for SavedStateRegistry requirement
            val savedStateRegistryOwner = object : SavedStateRegistryOwner {
                override val lifecycle = lifecycleOwner.lifecycle
                override val savedStateRegistry = SavedStateRegistryController.create(this).run {
                    performRestore(null)
                    savedStateRegistry
                }
            }
            it.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
            
            windowManager.addView(it, params)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        composeView?.let { windowManager.removeView(it) }
    }
}
