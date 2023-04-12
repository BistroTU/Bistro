package edu.temple.bistro.ui.friends

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import edu.temple.bistro.R

@Composable
fun ProfilePicture(imageId: Int) {
    Image(
        painter = painterResource(id = imageId),
        "profile picture",
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .border(2.dp, Color.White, CircleShape)
            .size(39.dp)
        ,
        contentScale = ContentScale.Crop
    )
}