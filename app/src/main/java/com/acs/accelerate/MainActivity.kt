package com.acs.accelerate

import android.Manifest
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
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

//        testLocation()

        testInternet()

        val imageView = findViewById<ImageView>(R.id.imageView)
        Glide.with(this)
            .load("https://developer.android.com/images/training/geofence_2x.png")
            .into(imageView)
    }

    private fun testLocation() {
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

    private fun testInternet() {
//       val status =  InternetUtils.isConnected(this)
//        Log.e("MainActivity", "Internet availability: $status")
//
//        InternetUtils.isConnected(this) { isWifi, isMobile ->
//            Log.e("MainActivity", "Internet availability- wifi: $isWifi, mobile: $isMobile")
//        }

        InternetUtils.listen(this) { isConnected, network ->
            Log.e("MainActivity", "Internet - isConnected: $isConnected")
        }
    }

    override fun onResume() {
        super.onResume()

//        locationHelper.requestLocationUpdates()
    }

    override fun onPause() {
        super.onPause()

//        locationHelper.cancelLocationUpdates()
    }
}