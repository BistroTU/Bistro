package edu.temple.bistro

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.temple.bistro.data.model.Restaurant
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class FirebaseHelper(private val db: FirebaseDatabase) {

    companion object {
        enum class FriendState {
            ACTIVE, PENDING_SENT, PENDING_RECEIVED
        }
    }

    fun addUser(uid: String, username: String, firstName: String, lastName: String) {
        val userRef = db.reference.child("users").child(uid)
        val userData = mapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "username" to username
        )
        userRef.setValue(userData)
            .addOnFailureListener {
                Log.d("ERROR", "Adding user $username unsuccessful.")
            }
    }

    fun setAgeBoolean(username: String, isOver21: Boolean) {
        val userRef = db.getReference("users").child(username).child("age_over_21")
        userRef.setValue(isOver21)
            .addOnFailureListener {
                Log.d("ERROR", "Setting boolean for $username unsuccessful.")
            }
    }

    fun setProfilePicture(username: String, picture: String) {
        val userRef = db.getReference("users").child(username).child("profile_picture")
        userRef.setValue(picture)
            .addOnFailureListener {
                Log.d("ERROR", "Setting profile picture for $username unsuccessful.")
            }
    }

    fun setFilterCriteria(username: String, filterCriteria: FilterCriteria) {
        val userRef = db.getReference("users").child(username).child("filter_criteria")
        userRef.setValue(filterCriteria)
            .addOnFailureListener {
                Log.d("ERROR", "Setting filter criteria for $username unsuccessful.")
            }
    }

    fun addLikedPlace(username: String, restaurant: Restaurant) {
        val userRef = db.getReference("users").child(username)
        val likedPlacesRef = userRef.child("liked_places")
        val likedCategoriesRef = userRef.child("liked_categories")
        val newPlaceRef = likedPlacesRef.child(restaurant.id)
        newPlaceRef.setValue(Place(restaurant.name, System.currentTimeMillis()))
            .addOnFailureListener {
                Log.d("ERROR", "Liking place ${restaurant.id} unsuccessful.")
            }
        getLikedCategories(username) {
            var newCategoryList = it
            for (category in restaurant.categories) {
                newCategoryList = newCategoryList + category.alias
            }
            likedCategoriesRef.setValue(newCategoryList)
        }
    }

    fun removeLikedPlace(username: String, restaurant: Restaurant) {
        val userRef = db.getReference("users").child(username)
        val placesRef = userRef.child("liked_places")
        placesRef.child(restaurant.id).setValue(null)
    }

    fun addDislikedPlace(username: String, restaurant: Restaurant) {
        val userRef = db.getReference("users").child(username)
        val dislikedPlacesRef = userRef.child("disliked_places")
        val newPlaceRef = dislikedPlacesRef.child(restaurant.id)
        newPlaceRef.setValue(Place(restaurant.name, System.currentTimeMillis())).addOnFailureListener {
            Log.d("ERROR", "Disliking place ${restaurant.id} unsuccessful.")
        }
    }

    fun removeDislikedPlace(username: String, restaurant: Restaurant) {
        val userRef = db.getReference("users").child(username)
        val placesRef = userRef.child("disliked_places")
        placesRef.child(restaurant.id).setValue(null)
    }

    fun addFriend(username: String, friend: Friend) {
        val userRef = db.getReference("users").child(username)
        val friendsRef = userRef.child("friends")
        val userNewFriendRef = friendsRef.child(friend.username)

        val friendRef = db.getReference("users").child(friend.username)
        val friendFriendsRef = friendRef.child("friends")
        val friendNewFriendRef = friendFriendsRef.child(username)

        var userHasFriend = false
        var friendHasUser = false

        runBlocking {
            checkFriendsList(username, friend.username) { result ->
                userHasFriend = result
            }

            checkFriendsList(friend.username, username) { result ->
                friendHasUser = result
            }
        }

        if(!userHasFriend && !friendHasUser) {
            userNewFriendRef.setValue(Friend(friend.username, FriendState.PENDING_SENT.name))
            friendNewFriendRef.setValue(Friend(username, FriendState.PENDING_RECEIVED.name))
        } else {
            userNewFriendRef.setValue(Friend(friend.username, FriendState.ACTIVE.name))
            friendNewFriendRef.setValue(Friend(username, FriendState.ACTIVE.name))
        }

    }

    fun removeFriend(username: String, friendUsername: String) {
        val userRef = db.getReference("users").child(username)
        val friendsRef = userRef.child("friends")
        friendsRef.child(friendUsername).setValue(null)

        val friendRef = db.getReference("users").child(friendUsername)
        val friendsFriendsRef = friendRef.child("friends")
        friendsFriendsRef.child(username).setValue(null)
    }

    fun createGroup(username: String) {
        val userRef = db.getReference("users").child(username)
        val groupsRef = db.getReference("groups")
        val groupID = generateGroupID()
        val members = listOf(username)
        val newGroup = Group(
            id = groupID,
            members = members
        )
        groupsRef.child(newGroup.id).setValue(newGroup)
        userRef.child("groups").child(groupID).setValue("")
            .addOnFailureListener {
            Log.d("ERROR", "Group creation unsuccessful.")
        }
    }

    fun joinGroup(username: String, groupID: String) {
        val userRef = db.getReference("users").child(username)
        val groupsRef = db.getReference("groups").child(groupID)
        groupsRef.child("members").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val members = dataSnapshot.value as? MutableList<String>
                if (members != null) {
                    members.add(username)
                    groupsRef.child("members").setValue(members)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        userRef.child("groups").child(groupID).setValue("")
            .addOnFailureListener {
                Log.d("ERROR", "Joining group $groupID unsuccessful.")
            }
    }

    fun leaveGroup(username: String, groupID: String){
        val userRef = db.getReference("users").child(username)
        val groupsRef = db.getReference("groups").child(groupID)
        groupsRef.child("members").child(username).removeValue()
            .addOnFailureListener {
                Log.d("ERROR", "Leaving group $groupID unsuccessful.")
            }
        userRef.child("groups").child(groupID).removeValue()
            .addOnFailureListener {
                Log.d("ERROR", "Leaving group $groupID unsuccessful.")
            }
    }

    fun deleteGroup(username: String, groupID: String){
        val userRef = db.getReference("users").child(username)
        val groupsRef = db.getReference("groups").child(groupID)
        groupsRef.removeValue()
            .addOnFailureListener {
                Log.d("ERROR", "Deleting group $groupID unsuccessful.")
            }
        userRef.child("groups").child(groupID).removeValue()
            .addOnFailureListener {
                Log.d("ERROR", "Leaving group $groupID unsuccessful.")
            }
    }

    private fun generateGroupID(): String {
        val alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ"
        val randomIndex = (alphabet.indices).random()
        val randomLetter = alphabet[randomIndex]
        val randomNumber = Random(System.currentTimeMillis()).nextInt(1000,10000)
        return "${randomLetter}${randomNumber}"
    }

    fun getUser(username: String, callback: (User?) -> Unit) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    callback(user)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getUsername(uid: String, callback: (String?) -> Unit) {
        val userRef = db.getReference("users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val username = dataSnapshot.child("username").getValue(String::class.java)
                    callback(username)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getName(username: String, callback: (String?) -> Unit) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val firstName = dataSnapshot.child("first_name").getValue(String::class.java)
                    val lastName = dataSnapshot.child("last_name").getValue(String::class.java)
                    val fullName = "$firstName $lastName"
                    callback(fullName)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getProfilePicture() {
        //TODO: implement if profile pics added
    }

    fun getOver21(username: String, callback: (Boolean) -> Unit) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val ageOver21 = dataSnapshot.child("age_over_21").getValue(Boolean::class.java)
                    callback(ageOver21 ?: false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AGE BOOLEAN", "Db error, returning false.")
                callback(false)
            }
        })
    }

    fun getFilterCriteria(username: String, callback: (FilterCriteria) -> Unit) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val filterCriteriaSnapshot = dataSnapshot.child("filter_criteria")
                    val filterCriteria = filterCriteriaSnapshot.getValue(FilterCriteria::class.java)
                    filterCriteria?.let { callback(it) }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getFriends(username: String, callback: (List<Friend>) -> Unit) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val friendsList = mutableListOf<Friend>()
                    val friendsSnapshot = dataSnapshot.child("friends")

                    friendsSnapshot.children.forEach { friendSnapshot ->
                        val friendName = friendSnapshot.child("name").getValue(String::class.java)
                        val friendStatus = friendSnapshot.child("friend_status").getValue(String::class.java)
                        val friend = Friend(friendName!!, friendStatus!!)
                        friendsList.add(friend)
                    }
                    callback(friendsList)
                } else {
                    callback(emptyList())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(emptyList())
            }
        })
    }
    
    fun getUserGroups(username: String, callback: (List<String>) -> Unit) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val groupsList = mutableListOf<String>()
                    val groupsSnapshot = dataSnapshot.child("groups")
                    groupsSnapshot.children.forEach { groupSnapshot ->
                        val groupCode = groupSnapshot.getValue(String::class.java)
                        if (groupCode != null) {
                            groupsList.add(groupCode)
                        }
                    }
                    callback(groupsList)
                } else {
                    callback(emptyList())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getLikedCategories(username: String, callback: (List<String>) -> Unit) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val categoryList = mutableListOf<String>()
                    val categorySnapshot = dataSnapshot.child("liked_categories")
                    categorySnapshot.children.forEach { categorySnapshot ->
                        val category = categorySnapshot.getValue(String::class.java)
                        if (category != null) {
                            categoryList.add(category)
                        }
                    }
                    callback(categoryList)
                } else {
                    callback(emptyList())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getLikedPlaces(username: String, callback: (List<Map<String, Any>>) -> Unit) {
        val userRef = db.getReference("users").child(username)
        userRef.child("liked_places").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val likedPlaces = mutableListOf<Map<String, Any>>()
                dataSnapshot.children.forEach { placeSnapshot ->
                    val place = mutableMapOf<String, Any>()
                    place["name"] = placeSnapshot.child("name").value.toString()
                    place["timestamp"] = placeSnapshot.child("timestamp").value as Long
                    likedPlaces.add(place)
                }
                callback(likedPlaces)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "Could not retrieve liked places.")
            }
        })
    }

    fun getDislikedPlaces(username: String, callback: (List<Map<String, Any>>) -> Unit) {
        val userRef = db.getReference("users").child(username)
        userRef.child("disliked_places").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dislikedPlaces = mutableListOf<Map<String, Any>>()
                dataSnapshot.children.forEach { placeSnapshot ->
                    val place = mutableMapOf<String, Any>()
                    place["name"] = placeSnapshot.child("name").value.toString()
                    place["timestamp"] = placeSnapshot.child("timestamp").value as Long
                    dislikedPlaces.add(place)
                }
                callback(dislikedPlaces)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", "Could not retrieve disliked places.")
            }
        })
    }

    private fun checkFriendsList(username: String, searchUsername: String, callback: (Boolean) -> Unit) {
        val userRef = db.getReference("users").child(username)
        val friendsRef = userRef.child("friends")

        friendsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var usernameFound = false
                for (friendSnapshot in snapshot.children) {
                    val friend = friendSnapshot.getValue(Friend::class.java)
                    if (friend?.username == searchUsername) {
                        usernameFound = true
                        break
                    }
                }
                callback(usernameFound)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}