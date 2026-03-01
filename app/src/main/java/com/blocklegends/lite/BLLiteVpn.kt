package com.blocklegends.lite

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import java.net.InetAddress

class BLLiteVpn : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Sunucu IP'sini yakala: 162.19.126.161
        val builder = Builder()
        builder.setSession("BL Lite Proxy")
        builder.addAddress("10.0.0.2", 32)
        builder.addDnsServer("8.8.8.8")
        
        // Sadece oyunun IP'sini yakala ve yönlendir
        builder.addRoute("162.19.126.161", 32) 
        
        vpnInterface = builder.establish()
        
        // Proxy işlemini başlat (Local port 9090)
        startProxy()
        
        return START_STICKY
    }

    private fun startProxy() {
        // Burada basit bir Socket Proxy çalışacak
    }

    override fun onDestroy() {
        vpnInterface?.close()
        super.onDestroy()
    }
}
