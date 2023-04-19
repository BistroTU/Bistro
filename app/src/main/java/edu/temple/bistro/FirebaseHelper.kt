package edu.temple.bistro

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random.Default.nextInt

class FirebaseHelper(private val db: FirebaseDatabase) {

    fun addUser(username: String, firstName: String, lastName: String) {
        val userRef = db.reference.child("users").child(username)
        val userData = mapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "username" to username
        )
        userRef.setValue(userData)
    }

    fun setAgeBoolean(username: String, isOver21: Boolean) {
        val userRef = db.getReference("users").child(username).child("age_over_21")
        userRef.setValue(isOver21)
    }

    fun setProfilePicture(username: String, picture: String) {
        val userRef = db.getReference("users").child(username).child("profile_picture")
        userRef.setValue(picture)
    }

    fun setFilterCriteria(username: String, filterCriteria: FilterCriteria) {
        val userRef = db.getReference("users").child(username).child("filter_criteria")
        userRef.setValue(filterCriteria)
    }


    fun addLikedPlace(username: String, placeID: String, place: Place) {
        val userRef = db.getReference("users").child(username)
        val likedPlacesRef = userRef.child("liked_places")
        val newPlaceRef = likedPlacesRef.child(placeID)
        newPlaceRef.setValue(place)
            .addOnSuccessListener {}
            .addOnFailureListener {}
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
                        .addOnSuccessListener {}
                        .addOnFailureListener {}
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addDislikedPlace(username: String, placeID: String, place: Place) {
        val userRef = db.getReference("users").child(username)
        val dislikedPlacesRef = userRef.child("disliked_places")
        val newPlaceRef = dislikedPlacesRef.child(placeID)
        newPlaceRef.setValue(place)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    //does not work
    fun removeDislikedPlace(username: String, placeID: String) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(User::class.java)
                if (userData != null) {
                    val friends = userData.disliked_places
                    val updatedFriends = friends.filterKeys { it != placeID }
                    userRef.child("disliked_places").setValue(updatedFriends)
                        .addOnSuccessListener {}
                        .addOnFailureListener {}
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addFriend(username: String, friendID: String, friend: Friend) {
        val userRef = db.getReference("users").child(username)
        val friendRef = userRef.child("friends")
        val newPlaceRef = friendRef.child(friendID)
        newPlaceRef.setValue(friend)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    //does not work
    fun removeFriend(username: String, friendID: String) {
        val userRef = db.getReference("users").child(username)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(User::class.java)
                if (userData != null) {
                    val friends = userData.friends
                    val updatedFriends = friends.filterKeys { it != friendID }
                    userRef.child("friends").setValue(updatedFriends)
                        .addOnSuccessListener {}
                        .addOnFailureListener {}
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getLikedPlaces(userId: String, callback: (List<Map<String, Any>>) -> Unit) {
        val userRef = db.getReference("users").child(userId)
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

    fun getDislikedPlaces(userId: String, callback: (List<Map<String, Any>>) -> Unit) {
        val userRef = db.getReference("users").child(userId)
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
    }

    private fun generateGroupID(): String {
        val alphabet = "ABCDEFGHJKMNPQRSTUVWXYZ"
        val randomIndex = (alphabet.indices).random()
        val randomLetter = alphabet[randomIndex]
        val randomNumber = nextInt(1000,10000)
        return "${randomLetter}${randomNumber}"
    }

}