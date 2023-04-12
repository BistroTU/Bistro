package edu.temple.bistro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexstyl.swipeablecard.Direction
import com.alexstyl.swipeablecard.SwipeableCardState
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import dagger.hilt.android.AndroidEntryPoint
import edu.temple.bistro.ui.navigation.*
import edu.temple.bistro.ui.restaurant.RestaurantCard
import edu.temple.bistro.ui.restaurant.RestaurantData
import edu.temple.bistro.ui.signup.SignUp
import edu.temple.bistro.ui.theme.BistroTheme
import androidx.compose.foundation.lazy.items

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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