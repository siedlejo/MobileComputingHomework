
package com.siedler.jonah.mobilecomputinghomework.helper.locations

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.siedler.jonah.mobilecomputinghomework.MyApplication
import com.siedler.jonah.mobilecomputinghomework.R
import com.siedler.jonah.mobilecomputinghomework.db.AppDB
import com.siedler.jonah.mobilecomputinghomework.db.reminder.Reminder
import com.siedler.jonah.mobilecomputinghomework.helper.PreferenceHelper
import com.siedler.jonah.mobilecomputinghomework.helper.notifications.NotificationHelper
import com.siedler.jonah.mobilecomputinghomework.ui.home.HomeFragment
import kotlin.properties.Delegates

private const val LOCATION_REFRESH_TIME: Long = 3000 // The Minimum Time to get location update in milliseconds
private const val LOCATION_REFRESH_DISTANCE: Float = 10f // The Minimum Distance to be changed to get location update in meters
const val LOCATION_PERMISSION_REQUEST_CODE = 23453
const val THRESHOLD_DISTANCE = 5000 // in meters

object LocationHelper {
    private var lastKnownLocation: Location? = null
    private var registeredReminderIdList: MutableSet<String> = HashSet()

    init {
        registeredReminderIdList = PreferenceHelper().getLocationReminderList()
    }

    // the permission check is handled in the function
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = LocationListener { location -> onLocationChanged(location) }
        if (!locationPermissionGranted(context)) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_REFRESH_TIME,
            LOCATION_REFRESH_DISTANCE,
            locationListener
        )
    }

    fun registerAllReminder(reminderList: List<Reminder>) {
        val idList = reminderList.map { it.reminderId }
        registeredReminderIdList.addAll(idList)
        PreferenceHelper().storeLocationReminderList(registeredReminderIdList)
    }

    fun registerReminder(reminder: Reminder) {
        if (reminder.locationX != null && reminder.locationY != null) {
            registeredReminderIdList.add(reminder.reminderId)
            PreferenceHelper().storeLocationReminderList(registeredReminderIdList)
        }
    }

    fun deregisterReminder(reminder: Reminder) {
        registeredReminderIdList.remove(reminder.reminderId)
    }

    private fun onLocationChanged(location: Location) {
        this.lastKnownLocation = location
        for (id: String in registeredReminderIdList) {
            val reminder = AppDB.getInstance().reminderDao().getReminder(id)
            if (reminder?.locationX != null && reminder.locationY != null) {
                if (isNear(reminder)) {
                    triggerNotification(reminder)
                }
            }
        }
        HomeFragment.Instance?.getReminderListFromDB()
    }

    fun isNear(reminder: Reminder): Boolean {
        if (lastKnownLocation == null || reminder.locationX == null || reminder.locationY == null) {
            return false
        }
        var result = FloatArray(1)
        Location.distanceBetween(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude, reminder!!.locationY!!, reminder!!.locationX!!, result)
        val distance = result[0]
        return distance < THRESHOLD_DISTANCE
    }

    private fun triggerNotification(reminder: Reminder) {
        if (!reminder.reminderSeen) {
            NotificationHelper.sendNotification(reminder.reminderId, reminder.message, MyApplication.instance.getString(R.string.reminder_location_description))
        }
    }

    fun requestLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun locationPermissionGranted(context: Context): Boolean {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val backgroundLocationGranted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fineLocationGranted && coarseLocationGranted && backgroundLocationGranted
    }
}