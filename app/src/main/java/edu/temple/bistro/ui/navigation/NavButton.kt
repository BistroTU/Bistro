package edu.temple.bistro.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun NavButton(icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick,
        modifier= Modifier.size(43.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF292929), contentColor = Color.White),
    ) {
        Icon(
            icon,
            "search bar"
        )
    }
}

@Composable
fun FriendsButton() {
    val mContext = LocalContext.current
    NavButton(Icons.Filled.People, { Toast.makeText(mContext, "Friends Button", Toast.LENGTH_LONG).show()})
}

@Composable
fun SettingsButton() {
    val mContext = LocalContext.current
    NavButton(Icons.Filled.Settings, { Toast.makeText(mContext, "Friends Button", Toast.LENGTH_LONG).show()})
}

