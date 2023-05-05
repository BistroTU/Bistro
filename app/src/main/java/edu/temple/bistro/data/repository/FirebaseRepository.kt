package edu.temple.bistro.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.temple.bistro.data.firebase.FirebaseCategory
import edu.temple.bistro.data.firebase.FirebaseFriend
import edu.temple.bistro.data.firebase.FirebaseGroup
import edu.temple.bistro.data.firebase.FirebasePlace
import edu.temple.bistro.data.firebase.FirebaseUser
import edu.temple.bistro.data.model.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirebaseRepository(private val db: FirebaseDatabase, private val context: Context) {
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

    //registers a user with firebase
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
                if (user?.groups != null && recurse) {
                    for (group in user.groups!!.keys) {
                        registerGroup(group)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FR: registerUser", "onCancelled", error.toException())
            }

        })
    }

    //adds a group to Firebase
    fun registerGroup(groupID: String) {
        Log.d("TR: regGroup", groupID)
        if (!groups.containsKey(groupID)) {
            groups[groupID] = MutableStateFlow(null)
        }
        db.getReference("groups").child(groupID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(FirebaseGroup::class.java)
                groups[groupID]!!.value = group
                if (group?.members != null) {
                    for (member in group.members!!) {
                        registerUser(member, false)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FR: registerGroup", "onCancelled", error.toException())
            }

        });
    }

    //returns StateFlow for a Firebase User
    fun getUserFlow(username: String): StateFlow<FirebaseUser?> {
        return if (users.containsKey(username)) {
            users[username]!!.asStateFlow()
        } else {
            users[username] = MutableStateFlow(null)
            users[username]!!.asStateFlow()
        }
    }

    //returns a FirebaseUser
    fun getUserBlocking(username: String): FirebaseUser? {
        return runBlocking {
            return@runBlocking db.getReference("users").child(keyStr(username)).get().await()
                .getValue(FirebaseUser::class.java)
        }
    }

    //returns FirebaseGroup Stateflow
    fun getGroupFlow(groupID: String): StateFlow<FirebaseGroup?> {
        return if (groups.containsKey(groupID)) {
            groups[groupID]!!.asStateFlow()
        } else {
            groups[groupID] = MutableStateFlow(null)
            groups[groupID]!!.asStateFlow()
        }
    }

    //returns a FirebaseGroup
    fun getGroupBlocking(groupID: String): FirebaseGroup? {
        return runBlocking {
            db.getReference("groups").child(groupID).get().await()
                .getValue(FirebaseGroup::class.java)
        }
    }

    //updates a FirebaseUser
    fun updateUser(user: FirebaseUser) {
        db.getReference("users").child(keyStr(user.username!!)).updateChildren(user.toMap())
    }

    //removed a Friendship between two FirebaseUsers
    fun removeFriendship(user1: String, user2: String) {
        val key1 = keyStr(user1)
        val key2 = keyStr(user2)
        db.getReference("users").updateChildren(mapOf(
            "/$key1/friends/$key2" to null,
            "/$key2/friends/$key1" to null
        ))
    }

    //adds a friendship between two firebase users
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

    //adds a user to firebase
    fun addUser(username: String, firstName: String, lastName: String) {
        val user = FirebaseUser(username = username, first_name = firstName, last_name = lastName)
        db.getReference("users").updateChildren(mapOf(
            "/${keyStr(username)}" to user.toMap()
        ))
    }

    //adds a place to a users liked places list in firebase
    fun addLikedPlace(user: FirebaseUser, restaurant: Restaurant) {
        val place = FirebasePlace(restaurant.id, restaurant.name, System.currentTimeMillis(), restaurant.url)
        if (user.liked_categories == null) {
            user.liked_categories = restaurant.categories.associate {
                Pair(it.alias, FirebaseCategory(it.alias, it.title))
            }.toMutableMap()
        }
        else {
            restaurant.categories.forEach {
                user.liked_categories!!.putIfAbsent(it.alias, FirebaseCategory(it.alias, it.title))
            }
        }
        if (user.liked_places == null) {
            user.liked_places = mutableMapOf(
                Pair(restaurant.id, place)
            )
        }
        else {
            user.liked_places!![restaurant.id] = place
        }
        updateUser(user)
    }

    //adds a place to a users disliked places list in firebase
    fun addDislikedPlace(user: FirebaseUser, restaurant: Restaurant) {
        val place = FirebasePlace(restaurant.id, restaurant.name, System.currentTimeMillis(), restaurant.url)
        if (user.disliked_places == null) {
            user.disliked_places = mutableMapOf(
                Pair(restaurant.id, place)
            )
        }
        else {
            user.disliked_places!![restaurant.id] = place
        }
        updateUser(user)
    }

    //creates a group in Firebase
    fun createGroup(user: FirebaseUser) {
        val groupID = generateGroupID()
        db.getReference("").updateChildren(mapOf(
            "/users/${keyStr(user.username!!)}/groups/$groupID" to true,
            "/groups/$groupID" to FirebaseGroup(id=groupID, members = mutableListOf(user.username!!)).toMap()
        ))
        registerGroup(groupID)
    }

    //allows a user to join a group in Firebase
    fun joinGroup(user: FirebaseUser, groupID: String) {
        val group = if (groups.containsKey(groupID)) {
            groups[groupID]!!.value
        } else {
            registerGroup(groupID)
            getGroupBlocking(groupID)
        }
        if (group == null) {
            Toast.makeText(context, "Invalid Group", Toast.LENGTH_SHORT).show()
            return
        }
        if (group.members == null) {
            group.members = mutableListOf(user.username!!)
        }
        else {
            group.members!!.add(user.username!!)
        }
        db.getReference("").updateChildren(mapOf(
            "/users/${keyStr(user.username!!)}/groups/$groupID" to true,
            "/groups/$groupID" to group.toMap()
        ))
    }

    //allows a user to leave a group in Firebase
    fun leaveGroup(user: FirebaseUser, groupID: String) {
        val group = groups[groupID]?.value
        if (group == null) {
            Toast.makeText(context, "Invalid Group", Toast.LENGTH_SHORT).show()
            return
        }
        if (group.members != null) {
            group.members!!.remove(user.username!!)
        }
        db.getReference("").updateChildren(mapOf(
            "/users/${keyStr(user.username!!)}/groups/$groupID" to null,
            "/groups/$groupID" to group.toMap()
        ))
    }

    //deletes a group from Firebase
    fun deleteGroup(groupID: String) {
        val group = groups[groupID]?.value
        if (group == null) {
            Toast.makeText(context, "Invalid Group", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedValues = mutableMapOf<String, Any?>()
        if (group.members != null) {
            group.members!!.forEach {
                updatedValues["/users/${keyStr(it)}/groups/$groupID"] = null
            }
        }
        updatedValues["/groups/$groupID"] = null
        db.getReference("").updateChildren(updatedValues)
    }

    //returns the common liked categories between two users
    fun getCommonCategories(usernames: List<String>): List<FirebaseCategory> {
        val users = usernames.map { users[it]?.value }
        var categories = setOf<FirebaseCategory>()

        users.forEach {
            if (it?.liked_categories == null) return@forEach
            categories = if (categories.isEmpty()) categories.union(it.liked_categories!!.values)
            else categories.intersect(it.liked_categories!!.values.toSet())
        }

        return categories.toList()
    }

    //returns the common liked places between two users
    fun getCommonPlaces(usernames: List<String>): MutableMap<String, FirebasePlace> {
        val users = usernames.map { users[it]?.value }
        var placeSet = setOf<String>()
        val places = mutableMapOf<String, FirebasePlace>()

        users.forEach {
            if (it?.liked_places == null) return@forEach
            placeSet = if (placeSet.isEmpty()) placeSet.union(it.liked_places!!.keys)
            else placeSet.intersect(it.liked_places!!.keys)
            it.liked_places!!.forEach { (id, place) ->
                if (placeSet.contains(id)) places[id] = place
            }
        }
        return places
    }

    //generates a random group id
    fun generateGroupID(): String {
        val alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ"
        val rand = Random(System.currentTimeMillis())
        val randomLetter = alphabet[rand.nextInt(0,alphabet.length)]
        val randomNumber = rand.nextInt(1000,10000)
        return "$randomLetter$randomNumber"
    }

}