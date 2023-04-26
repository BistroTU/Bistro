package edu.temple.bistro

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.core.content.ContextCompat.checkSelfPermission
import dagger.hilt.android.AndroidEntryPoint
import edu.temple.bistro.ui.navigation.*
import edu.temple.bistro.ui.restaurant.RestaurantCard
import edu.temple.bistro.ui.restaurant.RestaurantData
import edu.temple.bistro.ui.theme.BistroTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.navigation.NavigationItem
import edu.temple.bistro.ui.navigation.screens.SignUpScreen
import edu.temple.bistro.ui.signin.SignInScreen
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var currentLocation: Location? = null
    private val database = Firebase.database
    private val helper = FirebaseHelper(database)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbTest()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener {}

        if (checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 123)
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener
            )
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }

        val viewModel = ViewModelProvider(this).get(BistroViewModel::class.java)

        setContent {
            val navController = rememberNavController()

            val bottomNavigationItems = listOf(
                NavigationItem.HomeScreen,
                NavigationItem.FriendsScreen,
                NavigationItem.SettingsScreen
            )
            Scaffold(
                bottomBar = {
                    BottomNavbar(navController = navController, items = bottomNavigationItems)
                },
            ) {
                it
                SignInScreen()
            }
        }
    }

    private fun dbTest() {
        val username = "username"
        val placeId1 = "place-" + UUID.randomUUID().toString()
        val placeId2 = "place-" + UUID.randomUUID().toString()
        helper.addUser(username,"John", "Doe")
        helper.addLikedPlace(username, placeId1, Place("Mama Meatball", 1))
        helper.addLikedPlace(username, placeId2, Place("Mama!! Meatball!!", 2))
        helper.createGroup(username)
        helper.createGroup(username)
        helper.setAgeBoolean(username, false)
        helper.getLikedPlaces("username") { likedPlaces ->
            Log.d("LIKED PLACES", likedPlaces.toString())
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0f,
                    locationListener
                )
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BistroTheme {
        Greeting("Android")
    }
}