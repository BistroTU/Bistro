package edu.temple.bistro.ui.navigation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.alexstyl.swipeablecard.Direction
import edu.temple.bistro.ui.BistroViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavController, viewModel: BistroViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
//            val states = restaurants.reversed()
//                .map { it to rememberSwipeableCardState() }
            val newRestaurantStates = viewModel.yelpRepository.getNewRestaurants(5).collectAsState(initial = emptyList())
            val states = newRestaurantStates.value.map { it to rememberSwipeableCardState() }
            var hint by remember {
                mutableStateOf("Swipe a card or press a button below")
            }

            val scope = rememberCoroutineScope()
            Box(
                Modifier.fillMaxWidth()
            ) {
                states.forEach { (restaurant, state) ->
                    if (state.swipedDirection == null) {
                        RestaurantCard(
                            state = state,
                            data = restaurant,
                        )
                    }
                    LaunchedEffect(restaurant, state.swipedDirection) {
                        if (state.swipedDirection == Direction.Right || state.swipedDirection == Direction.Left) {
                            viewModel.yelpRepository.markRestaurantSeen(restaurant)
                        }
                    }
                }
            }
            Row(
                Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
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
                    icon = Icons.Rounded.Close
                )
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
                    icon = Icons.Rounded.Favorite
                )
                CircleButton(
                    onClick = {
                        viewModel.yelpRepository.fetchRestaurants()
                    },
                    icon = Icons.Rounded.Refresh
                )
            }
    }
}

@Composable
private fun CircleButton(
    onClick: () -> Unit,
    icon: ImageVector,
) {
    IconButton(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colors.primary)
            .size(56.dp)
            .border(2.dp, MaterialTheme.colors.primary, CircleShape),
        onClick = onClick
    ) {
        Icon(icon, null,
            tint = MaterialTheme.colors.onPrimary)
    }
}