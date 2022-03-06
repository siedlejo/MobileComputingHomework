package com.siedler.jonah.mobilecomputinghomework.ui.map

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.db.reminder.Reminder
import com.siedler.jonah.mobilecomputinghomework.helper.locations.LocationHelper
import com.siedler.jonah.mobilecomputinghomework.helper.locations.THRESHOLD_DISTANCE
import com.siedler.jonah.mobilecomputinghomework.ui.login.AuthenticationProvider

class ShowReminderLocationFragment: Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap
    private var locationMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout =  inflater.inflate(R.layout.fragment_reminder_location, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return layout
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMapClickListener(this)
        val initialPosition = LatLng(65.059352, 25.466309)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 13F))
    }

    override fun onMapClick(location: LatLng) {
        map.clear()

        map.addMarker(MarkerOptions().position(location))

        val reminderList = AppDB.getInstance().reminderDao().getAllReminderOfUser(
            AuthenticationProvider.getAuthenticatedUser()!!.userName)
        for (reminder: Reminder in reminderList) {
            if (reminder.locationX != null && reminder.locationY != null) {
                val position = LatLng(reminder!!.locationY!!, reminder!!.locationX!!)

                var result = FloatArray(1)
                Location.distanceBetween(location.latitude, location.longitude, reminder!!.locationY!!, reminder!!.locationX!!, result)
                val distance = result[0]

                if (distance < THRESHOLD_DISTANCE) {
                    map.addMarker(MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                }
            }
        }
    }
}