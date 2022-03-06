package com.siedler.jonah.mobilecomputinghomework.ui.reminder

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.siedler.jonah.mobilecomputinghomework.R


const val SELECTED_LOCATION_X = "SELECTED_LOCATION_X"
const val SELECTED_LOCATION_Y = "SELECTED_LOCATION_Y"

class SelectLocationActivity: AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap
    private var locationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.select_location_activity)

        mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.select_position, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.selectLocationButton -> {
                selectPosition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMapClickListener(this)
        val initialPosition = LatLng(65.059352, 25.466309)
        locationMarker = map.addMarker(MarkerOptions()
            .position(initialPosition))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 15F))
    }

    override fun onMapClick(location: LatLng) {
        locationMarker?.position = location
    }

    private fun selectPosition() {
        val resultIntent = Intent()
        val location = locationMarker!!.position
        resultIntent.putExtra(SELECTED_LOCATION_X, location.longitude)
        resultIntent.putExtra(SELECTED_LOCATION_Y, location.latitude)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}