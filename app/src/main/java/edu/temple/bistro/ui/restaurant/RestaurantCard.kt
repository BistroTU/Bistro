package edu.temple.bistro.ui.restaurant

import android.graphics.Paint.Align
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.layout.BoxScopeInstance.matchParentSize
//import androidx.compose.foundation.layout.BoxScopeInstance.matchParentSize
//import androidx.compose.foundation.layout.BoxScopeInstance.matchParentSize
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexstyl.swipeablecard.ExperimentalSwipeableCardApi
import com.alexstyl.swipeablecard.SwipeableCardState
import com.alexstyl.swipeablecard.rememberSwipeableCardState
import com.alexstyl.swipeablecard.swipableCard
import edu.temple.bistro.R
import edu.temple.bistro.ui.friends.ProfilePicture

@OptIn(ExperimentalSwipeableCardApi::class)
@Composable
fun RestaurantCard(data: RestaurantData, state: SwipeableCardState) {
    var sizeImage by remember { mutableStateOf(IntSize.Zero) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black),
        startY = sizeImage.height.toFloat()/3,  // 1/3
        endY = sizeImage.height.toFloat()
    )

    Card(
        elevation = 4.dp,
        modifier = Modifier
            .height(482.dp)
            .width(325.dp)
            .swipableCard(
                state = state,
                onSwiped = { direction ->
                    println("The card was swiped to $direction")
                },
                onSwipeCancel = {
                    println("The swiping was cancelled")
                }
            )

        ,
        shape = RoundedCornerShape(31.dp),
    ) {
        Box(){
            Image(painter = painterResource(id = R.drawable.dominos),
                contentDescription = "",
                modifier = Modifier
                    .onGloballyPositioned {
                        sizeImage = it.size
                    },
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth
                )
            Box(modifier = Modifier
                .matchParentSize()
                .background(gradient))
        }
//        Image(painter = painterResource(id = R.drawable.dominos), contentDescription = null)

        Box(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .width(IntrinsicSize.Min),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = data.name,
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = data.location,
                    fontSize = 20.sp,
                    color = Color.White,
                    fontStyle = FontStyle.Italic
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    ProfilePicture(imageId = R.drawable.dominos)
                    ProfilePicture(imageId = R.drawable.dominos)
                    ProfilePicture(imageId = R.drawable.dominos)
                    ProfilePicture(imageId = R.drawable.dominos)
                }
            }
        }

    }
}
