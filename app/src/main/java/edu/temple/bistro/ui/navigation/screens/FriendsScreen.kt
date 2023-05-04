package edu.temple.bistro.ui.navigation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import edu.temple.bistro.R
import edu.temple.bistro.data.repository.FirebaseRepository
import edu.temple.bistro.ui.BistroViewModel
import edu.temple.bistro.ui.friends.ProfilePicture
import edu.temple.bistro.ui.theme.Inter
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FriendsScreen(navController: NavController?, viewModel: BistroViewModel) {
    val userState = viewModel.firebaseUser.collectAsState()
    val scope = rememberCoroutineScope()

    val pendingRequests = userState.value?.friends?.values?.filter {
        it.friend_status == FirebaseRepository.FriendState.PENDING_RECEIVED.name
    } ?: emptyList()
    val activeFriends = userState.value?.friends?.values?.filter {
        it.friend_status == FirebaseRepository.FriendState.ACTIVE.name
    } ?: emptyList()
    val groups = userState.value?.groups?.keys?.toList() ?: emptyList()
    val selectedGroup = remember { mutableStateOf("") }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val addFriend = remember { mutableStateOf(false) }
                var friendEmail by rememberSaveable { mutableStateOf("") }
                Text(
                    text = "Friends",
                    fontSize = 30.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.baseline_person_add_24),
                    "Remove",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                        .clickable { addFriend.value = !addFriend.value },
                    contentScale = ContentScale.Crop
                )
                if (addFriend.value) {
                    AlertDialog(
                        onDismissRequest = { addFriend.value = false },
                        title = {
                            Text(text = "Add Friend")
                        },
                        text = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Enter the username of the person you'd like to add as a friend")
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = friendEmail,
                                    onValueChange = { friendEmail = it },
                                    placeholder = { Text(text = "Email") })
                            }

                        },
                        confirmButton = {
                            Button(onClick = {
                                addFriend.value = false
                                viewModel.fireRepo.addFriendship(
                                    userState.value!!.username!!,
                                    friendEmail
                                )
                                friendEmail = ""
                            }) {
                                Text(text = "Add Friend")
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                addFriend.value = false
                            }) {
                                Text(text = "Cancel")
                            }
                        }
                    )
                }
            }

            LazyColumn {
                if (pendingRequests.isNotEmpty()) {
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
                                    .size(64.dp),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = "Pending Requests",
                                fontSize = 24.sp,
                                fontFamily = Inter,
                                modifier = Modifier.weight(1f)
                            )
                            Image(
                                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                                "Arrow",
                                modifier = Modifier
                                    .padding(8.dp)
                                    .rotate(if (expanded.value) 0f else 90f)
                                    .size(32.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        if (expanded.value) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                for (friend in pendingRequests) {
                                    val friendUser =
                                        viewModel.fireRepo.getUserFlow(friend.username!!).value
                                    val name = "${friendUser?.first_name} ${friendUser?.last_name}"
                                    val openDialog = remember { mutableStateOf(false) }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
//                                        ProfilePicture(imageId = R.drawable.dominos)
                                        Text(
                                            text = name,
                                            modifier = Modifier
                                                .padding(4.dp)
                                                .weight(1f)
                                        )
                                        Image(
                                            painter = painterResource(id = R.drawable.baseline_add_24),
                                            "Add",
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .size(24.dp)
                                                .clickable { openDialog.value = true },
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
                                                Text(text = "Would you like to add ${name} as a friend?")
                                            },
                                            confirmButton = {
                                                Button(onClick = {
                                                    openDialog.value = false
                                                    viewModel.fireRepo.addFriendship(
                                                        userState.value!!.username!!,
                                                        friend.username!!
                                                    )
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

                items(items = activeFriends, itemContent = {
                    val name = remember { mutableStateOf("") }
                    it.username?.let { it1 ->
                        val friendUser = viewModel.fireRepo.getUserFlow(it1).value
                        name.value = "${friendUser?.first_name} ${friendUser?.last_name}"
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val openDialog = remember { mutableStateOf(false) }
//                        ProfilePicture(imageId = R.drawable.dominos)
                        Text(
                            text = name.value,
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(1f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            "Remove",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                                .clickable { openDialog.value = true },
                            contentScale = ContentScale.Crop
                        )
                        if (openDialog.value) {
                            AlertDialog(
                                onDismissRequest = { openDialog.value = false },
                                title = {
                                    Text(text = "Remove Friend")
                                },
                                text = {
                                    Text(text = "Would you like to remove ${name.value} as a friend?")
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        openDialog.value = false
                                        it.username?.let { un ->
                                            viewModel.fireRepo.removeFriendship(
                                                userState.value?.username!!,
                                                un
                                            )
                                        }
                                    }) {
                                        Text(text = "Remove Friend")
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
                })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val joinGroup = remember { mutableStateOf(false) }
                var groupID by rememberSaveable { mutableStateOf("") }
                Text(
                    text = "Groups",
                    fontSize = 30.sp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.baseline_groups_3_24),
                    "Join Group",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                        .clickable { joinGroup.value = !joinGroup.value },
                    contentScale = ContentScale.Crop
                )
                Image(
                    painter = painterResource(id = R.drawable.baseline_group_add_24),
                    "Add Group",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                        .clickable { viewModel.fireRepo.createGroup(userState.value!!) },
                    contentScale = ContentScale.Crop
                )
                if (joinGroup.value) {
                    AlertDialog(
                        onDismissRequest = { joinGroup.value = false },
                        title = {
                            Text(text = "Join Group")
                        },
                        text = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Enter the ID of the group you'd like to join")
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = groupID,
                                    onValueChange = { groupID = it },
                                    placeholder = { Text(text = "Group ID") })
                            }

                        },
                        confirmButton = {
                            Button(onClick = {
                                joinGroup.value = false
                                viewModel.fireRepo.joinGroup(userState.value!!, groupID.uppercase())
                                groupID = ""
                            }) {
                                Text(text = "Join Group")
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                joinGroup.value = false
                            }) {
                                Text(text = "Cancel")
                            }
                        }
                    )
                }
            }

            LazyColumn {

                items(items = groups, itemContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val openDialog = remember { mutableStateOf(false) }
//                        ProfilePicture()
                        Text(text = it,
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(1f)
                                .clickable {
                                    selectedGroup.value = it
                                }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.baseline_close_24),
                            "Remove",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                                .clickable { openDialog.value = true },
                            contentScale = ContentScale.Crop
                        )
                        if (openDialog.value) {
                            AlertDialog(
                                onDismissRequest = { openDialog.value = false },
                                title = {
                                    Text(text = "Leave Group")
                                },
                                text = {
                                    Text(text = "Would you like to leave group $it?")
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        openDialog.value = false
                                        viewModel.fireRepo.leaveGroup(userState.value!!, it)
                                    }) {
                                        Text(text = "Leave Group")
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
                })
            }
        }

        if (selectedGroup.value != "") {

            Column(
                modifier = Modifier
                    .background(Color(0xFFFFFFFF))
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(15.dp)
            ) {
                Text(text = selectedGroup.value, style = MaterialTheme.typography.h4, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp))


                Text(text = "Members", style = MaterialTheme.typography.h5, modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp))
                LazyColumn {
                    items(7) { member ->
                        Text(text = "username", style = MaterialTheme.typography.body1, modifier = Modifier.padding(start = 16.dp))
                    }
                }

                Text(text = "Overlapping Restaurants", style = MaterialTheme.typography.h5, modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp))
                LazyColumn {
                    items(7) { restaurant ->
                        Text(text = "restaurant name", style = MaterialTheme.typography.body1, modifier = Modifier.padding(start = 16.dp))
                    }
                }

                Text(text = "Overlapping Categories", style = MaterialTheme.typography.h5, modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp))
                LazyColumn {
                    items(7) { category ->
                        Text(text = "category name", style = MaterialTheme.typography.body1, modifier = Modifier.padding(start = 16.dp))
                    }
                }

            }

            IconButton(
                onClick = {selectedGroup.value=""},
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }
        }
    }

}
