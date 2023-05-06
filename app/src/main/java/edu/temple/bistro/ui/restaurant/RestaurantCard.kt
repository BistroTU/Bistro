package edu.temple.bistro.ui.restaurant

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.layout.BoxScopeInstance.matchParentSize
//import androidx.compose.foundation.layout.BoxScopeInstance.matchParentSize
//import androidx.compose.foundation.layout.BoxScopeInstance.matchParentSize
import androidx.compose.material.Card
import androidx.compose.material.Text
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
import coil.compose.AsyncImage
import com.alexstyl.swipeablecard.ExperimentalSwipeableCardApi
import com.alexstyl.swipeablecard.SwipeableCardState
import com.alexstyl.swipeablecard.swipableCard
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import edu.temple.bistro.R
import edu.temple.bistro.data.model.Restaurant
import edu.temple.bistro.ui.friends.ProfilePicture
import edu.temple.bistro.ui.theme.Inter
import edu.temple.bistro.ui.theme.MoneyGreen

@OptIn(ExperimentalSwipeableCardApi::class, ExperimentalLayoutApi::class)
@Composable
fun RestaurantCard(data: Restaurant, state: SwipeableCardState) {
    var sizeImage by remember { mutableStateOf(IntSize.Zero) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black),
        startY = 0f,  // 1/3
        endY = 1500f
    )

    Card(
        elevation = 0.dp,
        modifier = Modifier
            .height(570.dp)
            .width(370.dp)
            .swipableCard(
                state = state,
                blockedDirections = listOf(com.alexstyl.swipeablecard.Direction.Down),
                onSwiped = {
                    // swipes are handled by the LaunchedEffect
                    // so that we track button clicks & swipes
                    // from the same place
                    Log.d("Categories", data.categories.toString())
                },
                onSwipeCancel = {
                    Log.d("Swipeable-Card", "Cancelled swipe")
                }
            )

        ,
        shape = RoundedCornerShape(31.dp),
    ) {
        Box(){
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFFAAAAAA))
                    .onGloballyPositioned {
                        sizeImage = it.size
                    },
            ) {}
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop,
                model = data.imageUrl,
                contentDescription = "Translated description of what the image contains"
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
                    fontSize = 37.sp,
                    color = Color.White,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${data.location?.city.toString()}, ${data.location?.state.toString()}",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontFamily = Inter,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp),
                )
                FlowRow(
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp),
                    mainAxisAlignment = FlowMainAxisAlignment.Start,
                    mainAxisSize = SizeMode.Wrap,
                    mainAxisSpacing = 5.dp,
                    crossAxisSpacing = 6.dp,
                ) {
                    if (data.price != null) {
                        InfoChip(data.price.toString(), textColor = MoneyGreen, selected = false, onClick = {}, "")
                    }
                    data.categories.forEach { category -> InfoChip(category.title, selected = false, onClick = {}, chipContentDescription = "")}
                }
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(7.dp)
//                ) {
//                    ProfilePicture("Jacob Snarr")
//                    ProfilePicture("Jacob Snarr")
//                    ProfilePicture("Jacob Snarr")
//                    ProfilePicture("Jacob Snarr")
//                }
            }
        }

    }
}