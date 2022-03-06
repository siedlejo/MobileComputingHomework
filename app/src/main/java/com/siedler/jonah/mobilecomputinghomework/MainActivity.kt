package com.siedler.jonah.mobilecomputinghomework

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.siedler.jonah.mobilecomputinghomework.databinding.ActivityMainBinding
import com.siedler.jonah.mobilecomputinghomework.helper.locations.LOCATION_PERMISSION_REQUEST_CODE
import com.siedler.jonah.mobilecomputinghomework.helper.locations.LocationHelper
import com.siedler.jonah.mobilecomputinghomework.helper.locations.LocationService
import com.siedler.jonah.mobilecomputinghomework.ui.login.AuthenticationProvider
import com.siedler.jonah.mobilecomputinghomework.ui.login.LoginActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var initialLetterCircle: TextView
    private lateinit var usernameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!AuthenticationProvider.isLoggedIn()) {
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
            finish()
            return
        }

        setupView()
    }

    override fun onStart() {
        super.onStart()

        requestLocationPermission()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> logout()
            else -> {
                NavigationUI.onNavDestinationSelected(item, navController)
                drawerLayout.closeDrawers()
            }
        }

        return true
    }

    private fun setupView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        val firstName = AuthenticationProvider.getAuthenticatedUser()!!.firstName
        initialLetterCircle = binding.navView.getHeaderView(0).findViewById(R.id.initialLetterCircle)
        initialLetterCircle.text = firstName[0].toString().uppercase()
        usernameTextView = binding.navView.getHeaderView(0).findViewById(R.id.usernameTextView)
        usernameTextView.text = firstName
    }

    private fun requestLocationPermission() {
        LocationHelper.requestLocationPermission(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE ->  {
                if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.startForegroundService(applicationContext, Intent(applicationContext, LocationService::class.java))
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun logout() {
        AuthenticationProvider.logout()
        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
        finish()
    }
}