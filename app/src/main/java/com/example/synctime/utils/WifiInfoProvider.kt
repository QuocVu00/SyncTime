package com.example.synctime.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.ContextCompat

object WifiInfoProvider {

    const val PERMISSION_DENIED = "PERMISSION_DENIED"
    const val GPS_DISABLED = "GPS_DISABLED"
    const val WIFI_NOT_FOUND = "WIFI_NOT_FOUND"

    fun getBssid(context: Context): String {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return PERMISSION_DENIED
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!gpsEnabled && !networkEnabled) {
                return GPS_DISABLED
            }
        }

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val bssid = wifiManager.connectionInfo?.bssid

        return if (bssid.isNullOrBlank()) {
            WIFI_NOT_FOUND
        } else {
            bssid.uppercase()
        }
    }
}