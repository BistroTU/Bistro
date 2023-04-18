package edu.temple.bistro

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseHelper {

    fun addUser(db: FirebaseDatabase, user: String, firstName: String, lastName: String, username: String) {
        val userRef = db.reference.child("users").child(user)
        val userData = mapOf(
            "first_name" to firstName,
            "last_name" to lastName,
            "username" to username,
            "age_over_21" to false, // initialize with false
            "profile_picture" to null, // initialize with null
            "friends" to null, // initialize with null
            "filter_criteria" to mapOf(
                "min_rating" to null, // initialize with null
                "price_level" to null, // initialize with null
                "max_distance" to null, // initialize with null
                "categories" to null // initialize with null
            ),
            "liked_places" to null, // initialize with null
            "disliked_places" to null // initialize with null
        )
        userRef.setValue(userData)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun addLikedPlace(db: FirebaseDatabase, user: String, placeID: String, place: Place) {
        val userRef = db.getReference("users").child(user)
        val likedPlacesRef = userRef.child("liked_places")
        val newPlaceRef = likedPlacesRef.push()
        newPlaceRef.setValue(mapOf(placeID to place))
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



}