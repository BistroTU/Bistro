package edu.temple.bistro.ui.navigation.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import edu.temple.bistro.FirebaseHelper
import edu.temple.bistro.Friend
import edu.temple.bistro.R
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.friends.ProfilePicture
import edu.temple.bistro.ui.theme.Inter
import kotlinx.coroutines.launch

@Composable
fun FriendsScreen(navController: NavController?, viewModel: BistroViewModel) {
    val friendsState = viewModel.friends.collectAsState()
    val scope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text =  "Friends", fontSize = 30.sp, fontFamily = Inter, fontWeight = FontWeight.Bold)
        LazyColumn {
            if (friendsState.value.getFriends(FirebaseHelper.FriendState.PENDING_RECEIVED).isNotEmpty()) {
                item {
                    val expanded = remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier
                            .border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                            .clickable {
                                expanded.value = !expanded.value
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_person_add_24),
                            "Pending Requests",
                            modifier = Modifier
                                .clip(RoundedCornerShape(32.dp))
                                .padding(8.dp)
                                .size(64.dp)
                            ,
                            contentScale = ContentScale.Crop
                        )
                        Text(text = "Pending Requests", fontSize = 24.sp, fontFamily = Inter, modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                            "Arrow",
                            modifier = Modifier
                                .padding(8.dp)
                                .rotate(if (expanded.value) 0f else 90f)
                                .size(32.dp)
                            ,
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (expanded.value) {
                        Column( modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp) ) {
                            for (friend in friendsState.value.getFriends(FirebaseHelper.FriendState.PENDING_RECEIVED)) {
                                val name = remember {mutableStateOf("")}
                                val openDialog = remember { mutableStateOf(false) }
                                viewModel.firebase.getName(friend.username) {uname ->
                                    Log.d("Friends", "NewName $uname")
                                    if (uname == null) return@getName
                                    name.value = uname
                                }
                                Row (modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    ProfilePicture(imageId = R.drawable.dominos)
                                    Text(text = name.value,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(1f))
                                    Image(
                                        painter = painterResource(id = R.drawable.baseline_add_24),
                                        "Add",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(24.dp)
                                            .clickable { openDialog.value = true }
                                        ,
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                if (openDialog.value) {
                                    AlertDialog(
                                        onDismissRequest = { openDialog.value = false },
                                        title = {
                                            Text(text = "Add Friend")
                                        },
                                        text = {
                                            Text(text = "Would you like to add ${name.value} as a friend?")
                                        },
                                        confirmButton = {
                                            Button(onClick = {
                                                openDialog.value = false
                                                viewModel.firebase.addFriend(viewModel.currentUser.value!!.email!!, friend)
                                            }) {
                                                Text(text = "Add Friend")
                                            }
                                        },
                                        dismissButton = {
                                            Button(onClick = {
                                                openDialog.value = false
                                            }) {
                                                Text(text = "Cancel")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(items= friendsState.value.getFriends(FirebaseHelper.FriendState.ACTIVE), itemContent = {
                val name = remember {mutableStateOf("")}
                viewModel.firebase.getName(it.username) {uname ->
                    Log.d("Friends", "NewName $uname")
                    if (uname == null) return@getName
                    name.value = uname
                }
                Row (modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically) {
                    ProfilePicture(imageId = R.drawable.dominos)
                    Text(text = name.value)
                }
            })
        }
    }

}

fun List<Friend>?.getFriends(type: FirebaseHelper.FriendState): List<Friend> {
    if (this == null) return emptyList()
    return this.filter { it.friend_status == type.name }
}

