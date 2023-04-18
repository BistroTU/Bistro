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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.core.content.ContextCompat.checkSelfPermission
import dagger.hilt.android.AndroidEntryPoint
import edu.temple.bistro.ui.navigation.*
import edu.temple.bistro.ui.theme.BistroTheme
import androidx.compose.material.Scaffold
import androidx.navigation.compose.rememberNavController
import edu.temple.bistro.ui.navigation.NavigationItem

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var currentLocation: Location? = null


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
                Navigation(navController)
            }
//            val state = rememberSwipeableCardState()
//            val state2 = rememberSwipeableCardState()
//
//            val restaurantData1: RestaurantData = RestaurantData("Dominos Pizza", "University City, PA", arrayOf("pizza", "fast food", "delivery", "wings"), 0.3f, 5)
//            var restaurantDataList = remember { mutableListOf(restaurantData1, restaurantData1) }
//
//
//
//            LaunchedEffect(state.swipedDirection){
//                if(state.swipedDirection!=null) {
//                    restaurantDataList.removeAt(0)
//                    restaurantDataList.add(restaurantData1)
//                }
//            }
//
//            Column(
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.fillMaxSize()
//            ) {
//                Column(
//                    verticalArrangement = Arrangement.spacedBy(10.dp),
//                    modifier = Modifier.width(325.dp)
//                ) {
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(7.dp),
//                    ) {
//                        SearchBar()
//                        FriendsButton()
//                        SettingsButton()
//                    }
//                    Box {
//                        restaurantDataList.forEach { restaurantData ->
//                            RestaurantCard(data = restaurantData, state = state)
//                        }
//                    }
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.SpaceEvenly,
//                        modifier = Modifier.width(325.dp)
//                    ) {
//                        DislikeButton(state)
//                        LikeButton(state)
//                    }
//                }
//            }
        }
    }

    private fun dbTest() {
        val database = Firebase.database
        val usersRef = database.getReference("users")
        val userId = "some_user_id"
        val user = User(
            "John",
            "Doe",
            "johndoe",
            "some_id_token",
            true,
            "https://some.url/profile_picture.jpg",
            FilterCriteria(
                4.5,
                2,
                10,
                mapOf(
                    "restaurant" to true,
                    "cafe" to true,
                    "bar" to false
                )
            ),
            mapOf(
                "placeid1" to Place("place1", System.currentTimeMillis()),
                "placeid2" to Place("place2", System.currentTimeMillis())
            ),
            mapOf(
                "placeid3" to Place("place3", System.currentTimeMillis()),
                "placeid4" to Place("place4", System.currentTimeMillis())
            ),
            mapOf(
                "friendid1" to Friend("Jane Doe","accepted"),
                "friendid2" to Friend("Janice Joe","pending")
            )
        )
        usersRef.child(userId).setValue(user)

        usersRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.value
                Log.d("READ FROM DB", "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {}

        })
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