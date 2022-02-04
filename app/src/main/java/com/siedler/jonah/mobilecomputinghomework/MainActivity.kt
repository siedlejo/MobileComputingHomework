package com.siedler.jonah.mobilecomputinghomework

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.siedler.jonah.mobilecomputinghomework.databinding.ActivityMainBinding
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

        val firstName = AuthenticationProvider.getAuthenticatedUser().firstName
        initialLetterCircle = binding.navView.getHeaderView(0).findViewById(R.id.initialLetterCircle)
        initialLetterCircle.text = firstName[0].toString().uppercase()
        usernameTextView = binding.navView.getHeaderView(0).findViewById(R.id.usernameTextView)
        usernameTextView.text = firstName
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

    private fun logout() {
        AuthenticationProvider.logout()
        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
        finish()
    }
}