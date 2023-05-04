package edu.temple.bistro.ui.friends

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.temple.bistro.ui.theme.Teal200
import kotlin.random.Random

@Composable
fun ProfilePicture(fullName: String) {

    Text(
        modifier = Modifier
            .padding(16.dp)
            .drawBehind {
                drawCircle(
                    color = Color.Gray,
                    radius = this.size.maxDimension
                )
            },
        text = fullName.split(' ').get(0).first().toString() + fullName.split(' ').get(1).first().toString(),
        style = TextStyle(color = Color.White, fontSize = 20.sp)
    )
}

fun getInitials(fullName: String): List<Char> {
    return fullName.split(' ').map { it.first() }
}