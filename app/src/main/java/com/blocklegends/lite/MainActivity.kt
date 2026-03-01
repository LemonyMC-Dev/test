package com.blocklegends.lite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            LiteTheme {
                MainScreen(
                    onStartClick = { startHack() },
                    onRequestPermissions = { requestPermissions() }
                )
            }
        }
    }

    private fun requestPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }
    }

    private fun startHack() {
        // VpnService'i başlat
        val intent = android.net.VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, 0)
        } else {
            onActivityResult(0, ComponentActivity.RESULT_OK, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ComponentActivity.RESULT_OK) {
            startService(Intent(this, BLLiteVpn::class.java))
            startService(Intent(this, OverlayService::class.java))
        }
    }
}

@Composable
fun MainScreen(onStartClick: () -> Unit, onRequestPermissions: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BLOCK LEGENDS LITE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Cyan
            )
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onRequestPermissions,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("İZİNLERİ VER", color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = onStartClick,
                modifier = Modifier.width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F3460)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("HİLEYİ BAŞLAT", color = Color.White)
            }
        }
    }
}

@Composable
fun LiteTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
