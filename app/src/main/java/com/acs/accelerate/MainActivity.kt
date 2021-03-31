package com.acs.accelerate

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Prompt location permission
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001);

        val locationProvider = LocationProvider(this)
        Log.e("MainActivity", "Location provider: ${locationProvider}")
        locationProvider.getLastKnownLocation()?.addOnSuccessListener {
            Log.e("MainActivity", "Last known location: ${it}")
        }

    }
}