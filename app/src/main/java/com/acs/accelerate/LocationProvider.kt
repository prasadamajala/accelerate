package com.acs.accelerate

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationProvider(val context: Context) {
    val client = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission() = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED

    fun getLastKnownLocation(): Task<Location>? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ) {
            return client.lastLocation
        }

        return null
    }
}