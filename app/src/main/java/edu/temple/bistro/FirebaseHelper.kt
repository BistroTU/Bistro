package edu.temple.bistro

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random
import kotlin.random.Random.Default.nextInt

class FirebaseHelper(private val db: FirebaseDatabase) {

    companion object {
        enum class FriendState {
            ACTIVE, PENDING
        }
    }

    fun addUser(username: String, firstName: String, lastName: String) {
        val userRef = db.reference.child("users").child(username)
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


    fun addLikedPlace(username: String, placeID: String, place: Place) {
        val userRef = db.getReference("users").child(username)
        val likedPlacesRef = userRef.child("liked_places")
        val newPlaceRef = likedPlacesRef.child(placeID)
        newPlaceRef.setValue(place)
            .addOnFailureListener {
                Log.d("ERROR", "Liking place $placeID unsuccessful.")
            }
    }

    //does not work
    fun removeLikedPlace(username: String, placeID: String) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(User::class.java)
                if (userData != null) {
                    val friends = userData.liked_places
                    val updatedFriends = friends.filterKeys { it != placeID }
                    userRef.child("liked_places").setValue(updatedFriends)
                        .addOnFailureListener {
                            Log.d("ERROR", "Removing liked place $placeID unsuccessful.")
                        }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR", error.toString())
            }
        })
    }

    fun addDislikedPlace(username: String, placeID: String, place: Place) {
        val userRef = db.getReference("users").child(username)
        val dislikedPlacesRef = userRef.child("disliked_places")
        val newPlaceRef = dislikedPlacesRef.child(placeID)
        newPlaceRef.setValue(place).addOnFailureListener {
            Log.d("ERROR", "Disliking place $placeID unsuccessful.")
        }
    }

    //TODO: does not work
//    fun removeDislikedPlace(username: String, placeID: String) {
//        val userRef = db.getReference("users").child(username)
//        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val userData = snapshot.getValue(User::class.java)
//                if (userData != null) {
//                    val friends = userData.disliked_places
//                    val updatedFriends = friends.filterKeys { it != placeID }
//                    userRef.child("disliked_places").setValue(updatedFriends)
//                        .addOnFailureListener {
//                            Log.d("ERROR", "Removing disliked place $placeID unsuccessful.")
//                        }
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("ERROR", error.toString())
//            }
//        })
//    }

    fun addFriend(username: String, friend: Friend) {
        val userRef = db.getReference("users").child(username)
        val friendRef = userRef.child("friends")
        val newPlaceRef = friendRef.child(friend.username)
        newPlaceRef.setValue(friend)
            .addOnFailureListener {
                Log.d("ERROR", "Adding friend ${friend.username} unsuccessful.")
        }
    }

    //TODO: does not work
//    fun removeFriend(username: String, friendUsername: String) {
//        val userRef = db.getReference("users").child(username)
//        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val userData = snapshot.getValue(User::class.java)
//                if (userData != null) {
//                    val friends = userData.friends
//                    val updatedFriends = friends.filterKeys { it != friendUsername }
//                    userRef.child("friends").setValue(updatedFriends)
//                        .addOnSuccessListener {}
//                        .addOnFailureListener {}
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("ERROR", "Removing friend $friendUsername unsuccessful.")
//            }
//        })
//    }

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

    fun getOver21() {
        //TODO: implement
    }

    fun getFilterCriteria() {
        //TODO: implement
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
    
    fun getUserGroups() {
        //TODO: implement
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
}