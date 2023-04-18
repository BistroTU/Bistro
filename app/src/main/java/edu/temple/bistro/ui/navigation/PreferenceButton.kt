package edu.temple.bistro.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.alexstyl.swipeablecard.Direction
import com.alexstyl.swipeablecard.SwipeableCardState
import edu.temple.bistro.R
import kotlinx.coroutines.launch

@Composable
fun PreferenceButton(backgroundColor: Color, textColor: Color, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick,
        modifier= Modifier.size(88.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = backgroundColor, contentColor = textColor),
    ) {
        Icon(
            icon,
            "preference button"
        )
    }
}

@Composable
fun LikeButton(state: SwipeableCardState) {
    val scope = rememberCoroutineScope()
    PreferenceButton(
        backgroundColor = Color(0xFFA2FA94),
        textColor = Color(0xFF5ABA4A),
        icon = Icons.Filled.ThumbUp,
        {
            scope.launch {
                state.swipe(Direction.Right)
            }
        }
    )
}

@Composable
fun DislikeButton(state: SwipeableCardState) {
    val scope = rememberCoroutineScope()
    PreferenceButton(
        backgroundColor = Color(0xFFF7AEAE),
        textColor = Color(0xFFDA5D5D),
        icon = Icons.Filled.ThumbDown,
        {
            scope.launch {
                state.swipe(Direction.Left)
            }
        })
}