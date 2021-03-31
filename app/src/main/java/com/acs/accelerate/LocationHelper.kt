package com.acs.accelerate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Helper class to access device location such as
 *  - Get last known location
 *  - Get continuous location updates
 *  - Get address from location ..etc
 *
 * Gradle dependency:
 *  implementation 'com.google.android.gms:play-services-location:18.0.0'
 *
 * Permissions:
 *  - Foreground
 *  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 *  <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 *
 *  - Background [Android 10 (API Level 29) and higher]
 *  <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
 *
 *
    Usage:
            val locationHelper = LocationHelper(this)
            locationHelper.request?.interval = TimeUnit.SECONDS.toMillis(8)
            locationHelper.listener = locationListener
            locationHelper.fetchLastKnownLocation {
                Log.e("MainActivity", "Last known location: $it")
            }
 */
class LocationHelper(private val context: Context) {

    // Listener to get continuous location updates
    lateinit var listener: (Location?) -> Unit

    // Default location configuration
    var request = LocationRequest.create()?.apply {
        interval = TimeUnit.SECONDS.toMillis(10)
        fastestInterval = TimeUnit.SECONDS.toMillis(5)
        priority = LocationRequest.PRIORITY_LOW_POWER
    }

    // Fused location provider
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                listener(location)
            }
        }
    }

    val geocoder: Geocoder = Geocoder(context, Locale.getDefault())

    /**
     * Get last known location of a device
     */
    fun fetchLastKnownLocation(onDone: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("App requires location permission")
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            onDone(location)
        }
    }

    /**
     * Get addresses from location
     */
    fun getAddress(location: Location, maxResults: Int = 1 ): List<Address>? {
        return geocoder.getFromLocation(location.latitude, location.longitude, maxResults).toList()
    }

    /**
     * Requests continuous location updates
     */
    fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("App requires location permission")
            return
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * Cancels continuous location updates
     */
    fun cancelLocationUpdates(): Task<Void> {
        return fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        const val TAG = "LocationHelper"
    }
}