package edu.temple.bistro

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.core.content.ContextCompat.checkSelfPermission
import dagger.hilt.android.AndroidEntryPoint
import edu.temple.bistro.ui.navigation.*
import edu.temple.bistro.ui.theme.BistroTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.navigation.NavigationItem
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var viewModel: BistroViewModel
    private var requestTwice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = LocationListener {}

        viewModel = ViewModelProvider(this)[BistroViewModel::class.java]

        dbTest()

        requestLocationPermission()

        setContent {
            val cm = LocalContext.current.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            OfflineRedirector(
                cm = cm,
                redirectComposable = { OfflineScreen() },
                content = { MainUI() }
            )

            val user = viewModel.currentUser.collectAsState()
            var startScreen = NavigationItem.SignUpScreen.route

            if (user.value != null) {
                Log.d("User: ", user.value.toString())
                startScreen = NavigationItem.HomeScreen.route
            }

            BistroTheme {
                Scaffold(
                    topBar = {
                      HomeTopBar(viewModel = viewModel)
                    },
                    bottomBar = {
                        BottomNavbar(navController = navController, items = bottomNavigationItems)
                    },
                ) { innerPadding ->
                    Navigation(navController, startScreen, viewModel, innerPadding)
                }
            }
        }
    }

    private fun dbTest() {
//        val username = "username"
//        val placeId1 = "place-" + UUID.randomUUID().toString()
//        val placeId2 = "place-" + UUID.randomUUID().toString()
//        viewModel.firebase.addUser(username,"John", "Doe")
//        viewModel.firebase.addLikedPlace(username, placeId1, Place("Mama Meatball", 1))
//        viewModel.firebase.addLikedPlace(username, placeId2, Place("Mama!! Meatball!!", 2))
//        viewModel.firebase.createGroup(username)
//        viewModel.firebase.createGroup(username)
//        viewModel.firebase.setAgeBoolean(username, false)
//        viewModel.firebase.getLikedPlaces("username") { likedPlaces ->
//            Log.d("LIKED PLACES", likedPlaces.toString())
//        }
    }

    private fun requestLocationPermission() {
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
            viewModel.location.value = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
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
                viewModel.location.value = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } else {
                Toast.makeText(
                    this,
                    "Bistro needs access to location information to function properly.",
                    Toast.LENGTH_LONG
                ).show()
                if (!requestTwice) {
                    requestLocationPermission()
                    requestTwice = true
                } else {
                    finish()
                }
            }
        }
    }

    @Composable
    fun MainUI() {
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
            Navigation(navController, viewModel)
        }
    }

    @Composable
    fun OfflineRedirector(
        cm: ConnectivityManager,
        redirectComposable: @Composable () -> Unit,
        content: @Composable () -> Unit
    ) {
        val isOnline = remember { mutableStateOf(false) }
        LaunchedEffect(cm) {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    isOnline.value = false
                }

                override fun onAvailable(network: Network) {
                    isOnline.value = true
                }
            }
            cm.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        }
        if (isOnline.value) {
            content()
        } else {
            redirectComposable()
        }
    }

    @Composable
    fun OfflineScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bistro needs internet access to work properly.",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}