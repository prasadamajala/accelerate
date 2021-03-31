package com.acs.accelerate

import android.Manifest
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var locationHelper: LocationHelper

    var TAG = "MainActivity"

    private val locationListener: (Location?) -> Unit = {
        Log.e("MainActivity", "New location: $it")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Prompt location permission
//        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)

        locationHelper = LocationHelper(this)
        locationHelper.request?.interval = TimeUnit.SECONDS.toMillis(8)
        locationHelper.listener = locationListener
        locationHelper.fetchLastKnownLocation {
            Log.e("MainActivity", "Last known location: $it")
            if (it != null) {
                val addresses = locationHelper.getAddress(it)
                Log.e("MainActivity", "Last known address: $addresses")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        locationHelper.requestLocationUpdates()
    }

    override fun onPause() {
        super.onPause()

        locationHelper.cancelLocationUpdates()
    }
}