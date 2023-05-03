package edu.temple.bistro.ui.navigation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import edu.temple.bistro.ui.restaurant.RestaurantCard
import edu.temple.bistro.ui.restaurant.RestaurantData
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexstyl.swipeablecard.Direction
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.navigation.HomeTopBar
import edu.temple.bistro.ui.restaurant.FilterMenu
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, viewModel: BistroViewModel, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(innerPadding),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
//            val states = restaurants.reversed()
//                .map { it to rememberSwipeableCardState() }
            val newRestaurantStates = viewModel.yelpRepository.newRestaurants.collectAsState(initial = emptyList())
            val states = newRestaurantStates.value.reversed().map { it to rememberSwipeableCardState() }
            var hint by remember {
                mutableStateOf("Swipe a card or press a button below")
            }
            val isFilterMenuOpen = remember { mutableStateOf(false) }

            val scope = rememberCoroutineScope()

            HomeTopBar(viewModel = viewModel, isFilterMenuOpen)
            Box {
                Column(modifier = Modifier.blur(if (isFilterMenuOpen.value) 30.dp else 0.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box {
                            states.forEach { (restaurant, state) ->
                                if (state.swipedDirection == null) {
                                    RestaurantCard(
                                        state = state,
                                        data = restaurant,
                                    )
                                }
                                LaunchedEffect(restaurant, state.swipedDirection) {
                                    if (state.swipedDirection == Direction.Right || state.swipedDirection == Direction.Left) {
                                        Log.d("MarkSeen", "hello ${restaurant.name}")
                                        viewModel.yelpRepository.markRestaurantSeen(restaurant)
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        Modifier
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Close Button
                        CircleButton(
                            onClick = {
                                scope.launch {
                                    val last = states.reversed()
                                        .firstOrNull {
                                            it.second.offset.value == Offset(0f, 0f)
                                        }?.second
                                    last?.swipe(com.alexstyl.swipeablecard.Direction.Left)
                                }
                            },
                            icon = Icons.Filled.Close,
                            iconColor = Color(0xFFDA5D5D)
                        )

                        // Check Button
                        CircleButton(
                            onClick = {
                                scope.launch {
                                    val last = states.reversed()
                                        .firstOrNull {
                                            it.second.offset.value == Offset(0f, 0f)
                                        }?.second

                                    last?.swipe(Direction.Right)
                                }
                            },
                            icon = Icons.Filled.Check,
                            iconColor = Color(0xFF5ABA4A)
                        )

                        // Refresh Button
                        CircleButton(
                            onClick = {
                                viewModel.yelpRepository.fetchRestaurants()
                            },
                            icon = Icons.Rounded.Refresh,
                            iconColor = Color.Gray
                        )
                    }
                }

                if (isFilterMenuOpen.value) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Color(0xAAFFFFFF))
                    ) {
                        FilterMenu(bistroViewModel = viewModel)
                    }
                }
            }
    }
}

@Composable
private fun CircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
    iconColor: Color
) {
    IconButton(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White)
            .size(88.dp)
            .border(2.dp, Color.hsv(0f, 0f, 0.95f, 1f), CircleShape),
        onClick = onClick
    ) {
        Icon(icon, null,
            tint = iconColor, modifier = Modifier.size(48.dp))
    }
}

@Composable
@Preview
fun CircleButtonPreview() {
    CircleButton(onClick = { /*TODO*/ }, icon = Icons.Filled.Close, iconColor = Color.Red)
}