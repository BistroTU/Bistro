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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.compose.ui.unit.dp
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import dagger.hilt.android.AndroidEntryPoint
import edu.temple.bistro.ui.navigation.*
import edu.temple.bistro.ui.restaurant.RestaurantCard
import edu.temple.bistro.ui.restaurant.RestaurantData
import edu.temple.bistro.ui.theme.BistroTheme
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

        setContent {
            val state = rememberSwipeableCardState()
            val state2 = rememberSwipeableCardState()

            val restaurantData1: RestaurantData = RestaurantData("Dominos Pizza", "University City, PA", arrayOf("pizza", "fast food", "delivery", "wings"), 0.3f, 5)
            var restaurantDataList = remember { mutableListOf(restaurantData1, restaurantData1) }



            LaunchedEffect(state.swipedDirection){
                if(state.swipedDirection!=null) {
                    restaurantDataList.removeAt(0)
                    restaurantDataList.add(restaurantData1)
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.width(325.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        SearchBar()
                        FriendsButton()
                        SettingsButton()
                    }
                    Box {
                        restaurantDataList.forEach { restaurantData ->
                            RestaurantCard(data = restaurantData, state = state)
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.width(325.dp)
                    ) {
                        DislikeButton(state)
                        LikeButton(state)
                    }
                }
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