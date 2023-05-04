package edu.temple.bistro.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.temple.bistro.data.firebase.FirebaseFriend
import edu.temple.bistro.data.firebase.FirebaseGroup
import edu.temple.bistro.data.firebase.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirebaseRepository(private val db: FirebaseDatabase) {
    private val users = mutableMapOf<String, MutableStateFlow<FirebaseUser?>>()
    private val groups = mutableMapOf<String, MutableStateFlow<FirebaseGroup?>>()

    enum class FriendState {
        ACTIVE, PENDING_SENT, PENDING_RECEIVED
    }

    companion object {
        fun keyStr(email: String): String {
            return email.replace('.', '_')
        }
    }

    fun registerUser(username: String, recurse: Boolean = true) {
        if (!users.containsKey(username)) {
            users[username] = MutableStateFlow(null)
        }
        db.getReference("users").child(keyStr(username)).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(FirebaseUser::class.java)
                users[username]!!.value = user
                if (user?.friends != null && recurse) {
                    for (friend in user.friends!!.values) {
                        friend.username?.let { registerUser(it, false) }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FR: registerUser", "onCancelled", error.toException())
            }

        });
    }

    fun getUserFlow(username: String): StateFlow<FirebaseUser?> {
        return if (users.containsKey(username)) {
            users[username]!!.asStateFlow()
        } else {
            users[username] = MutableStateFlow(null)
            users[username]!!.asStateFlow()
        }
    }

    fun getUserBlocking(username: String): FirebaseUser? {
        return runBlocking {
            return@runBlocking db.getReference("users").child(keyStr(username)).get().await()
                .getValue(FirebaseUser::class.java)
        }
    }

    fun updateUser(user: FirebaseUser) {
        db.getReference("users").child(keyStr(user.username!!)).updateChildren(user.toMap())
    }

    fun removeFriendship(user1: String, user2: String) {
        val key1 = keyStr(user1)
        val key2 = keyStr(user2)
        db.getReference("users").updateChildren(mapOf(
            "/$key1/friends/$key2" to null,
            "/$key2/friends/$key1" to null
        ))
    }

    fun addFriendship(sender: String, recipient: String) {
        val sendKey = keyStr(sender)
        val recvKey = keyStr(recipient)
        val senderHasFriend = users[sender]!!.value!!.friends?.containsKey(keyStr(recipient)) ?: false
        val recipientHasFriend = if (!users.containsKey(recipient)) {
            registerUser(recipient)
            getUserBlocking(recipient)?.friends?.containsKey(keyStr(sender)) ?: false
        } else {
            users[recipient]!!.value!!.friends?.containsKey(keyStr(sender)) ?: false
        }
        val updateValues = if (!senderHasFriend && !recipientHasFriend) {
            mapOf(
                "/$sendKey/friends/$recvKey" to FirebaseFriend(recipient, FriendState.PENDING_SENT.name).toMap(),
                "/$recvKey/friends/$sendKey" to FirebaseFriend(sender, FriendState.PENDING_RECEIVED.name).toMap()
            )
        } else {
            mapOf(
                "/$sendKey/friends/$recvKey" to FirebaseFriend(recipient, FriendState.ACTIVE.name).toMap(),
                "/$recvKey/friends/$sendKey" to FirebaseFriend(sender, FriendState.ACTIVE.name).toMap()
            )
        }
        db.getReference("users").updateChildren(updateValues)
    }

    fun addUser(username: String, firstName: String, lastName: String) {
        val user = FirebaseUser(username = username, first_name = firstName, last_name = lastName)
        db.getReference("users").updateChildren(mapOf(
            "/${keyStr(username)}" to user.toMap()
        ))
    }

    private fun generateGroupID(): String {
        val alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ"
        val rand = Random(System.currentTimeMillis())
        val randomLetter = alphabet[rand.nextInt(0,alphabet.length)]
        val randomNumber = rand.nextInt(1000,10000)
        return "$randomLetter$randomNumber"
    }

}