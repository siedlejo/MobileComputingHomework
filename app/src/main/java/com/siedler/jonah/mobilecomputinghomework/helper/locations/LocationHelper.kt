
package com.siedler.jonah.mobilecomputinghomework.helper.locations

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat

private const val LOCATION_REFRESH_TIME: Long = 3000 // The Minimum Time to get location update in milliseconds
private const val LOCATION_REFRESH_DISTANCE: Float = 10f // The Minimum Distance to be changed to get location update in meters
const val LOCATION_PERMISSION_REQUEST_CODE = 23453

object LocationHelper {
    private var lastKnownLocation: Location? = null

    // the permission check is handled in the function
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = LocationListener { location -> onLocationChanged(location) }
        if (!locationPermissionGranted(context)) {
            Toast.makeText(context, "TODO no permission granted", Toast.LENGTH_LONG)
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_REFRESH_TIME,
            LOCATION_REFRESH_DISTANCE,
            locationListener
        )
    }

    private fun onLocationChanged(location: Location) {
        this.lastKnownLocation = location
        println(location)
    }

    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun locationPermissionGranted(context: Context): Boolean {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocationGranted && coarseLocationGranted
    }
}