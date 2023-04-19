package edu.temple.bistro

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random.Default.nextInt

class FirebaseHelper {

    fun addUser(db: FirebaseDatabase, userID: String, firstName: String, lastName: String, username: String) {
        val userRef = db.reference.child("users").child(userID)
        val userData = mapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "username" to username
        )
        userRef.setValue(userData)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun addLikedPlace(db: FirebaseDatabase, user: String, placeID: String, place: Place) {
        val userRef = db.getReference("users").child(user)
        val likedPlacesRef = userRef.child("liked_places")
        val newPlaceRef = likedPlacesRef.child(placeID)
        newPlaceRef.setValue(place)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun removeLikedPlace(db: FirebaseDatabase, userID: String, placeID: String) {
        val userRef = db.getReference("users").child(userID)
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

    fun addDislikedPlace(db: FirebaseDatabase, user: String, placeID: String, place: Place) {
        val userRef = db.getReference("users").child(user)
        val dislikedPlacesRef = userRef.child("disliked_places")
        val newPlaceRef = dislikedPlacesRef.push()
        newPlaceRef.setValue(mapOf(placeID to place))
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun removeDislikedPlace(db: FirebaseDatabase, userID: String, placeID: String) {
        val userRef = db.getReference("users").child(userID)
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

    fun addFriend(db: FirebaseDatabase, user: String, friendID: String, friend: Friend) {
        val userRef = db.getReference("users").child(user)
        val friendRef = userRef.child("friends")
        val newPlaceRef = friendRef.push()
        newPlaceRef.setValue(mapOf(friendID to friend))
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun removeFriend(db: FirebaseDatabase, userID: String, friendID: String) {
        val userRef = db.getReference("users").child(userID)
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

    fun getLikedPlaces(db: FirebaseDatabase, userID: String): MutableList<Place> {
        val usersRef = db.getReference("users")
        val userRef = usersRef.child(userID)
        val likedPlacesRef = userRef.child("places").child("liked_places")
        val likedPlacesList = mutableListOf<Place>()
        likedPlacesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (placeSnapshot in snapshot.children) {
                    val placeName = placeSnapshot.child("name").getValue(String::class.java)
                    val placeTimestamp = placeSnapshot.child("timestamp").getValue(Long::class.java)
                    val place = Place(placeName!!, placeTimestamp!!)
                    likedPlacesList.add(place)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve liked places: ${error.message}")
            }
        })
        return likedPlacesList
    }

    fun getDislikedPlaces(db: FirebaseDatabase, userID: String): MutableList<Place> {
        val usersRef = db.getReference("users")
        val userRef = usersRef.child(userID)
        val likedPlacesRef = userRef.child("places").child("disliked_places")
        val likedPlacesList = mutableListOf<Place>()
        likedPlacesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (placeSnapshot in snapshot.children) {
                    val placeName = placeSnapshot.child("name").getValue(String::class.java)
                    val placeTimestamp = placeSnapshot.child("timestamp").getValue(Long::class.java)
                    val place = Place(placeName!!, placeTimestamp!!)
                    likedPlacesList.add(place)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to retrieve disliked places: ${error.message}")
            }
        })
        return likedPlacesList
    }

    fun createGroup(db: FirebaseDatabase, userID: String) {
        val userRef = db.getReference("users").child(userID)
        val groupsRef = db.getReference("groups")
        val groupID = generateGroupID()
        val members = listOf(userID)
        val newGroup = Group(
            id = groupID,
            members = members
        )
        groupsRef.child(newGroup.id).setValue(newGroup)
        userRef.child("groups").child(groupID).setValue("")
    }

    fun joinGroup(db: FirebaseDatabase, userID: String, groupID: String) {
        val userRef = db.getReference("users").child(userID)
        val groupsRef = db.getReference("groups").child(groupID)

        groupsRef.child("members").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val members = dataSnapshot.value as? MutableList<String>
                if (members != null) {
                    members.add(userID)
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