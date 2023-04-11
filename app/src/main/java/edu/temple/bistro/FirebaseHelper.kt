package edu.temple.bistro

import com.google.firebase.database.FirebaseDatabase

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

    fun addDislikedPlace(db: FirebaseDatabase, user: String, placeID: String, place: Place) {
        val userRef = db.getReference("users").child(user)
        val dislikedPlacesRef = userRef.child("disliked_places")
        val newPlaceRef = dislikedPlacesRef.push()
        newPlaceRef.setValue(mapOf(placeID to place))
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun addFriend(db: FirebaseDatabase, user: String, friendID: String, friend: Friend) {
        val userRef = db.getReference("users").child(user)
        val friendRef = userRef.child("friends")
        val newPlaceRef = friendRef.push()
        newPlaceRef.setValue(mapOf(friendID to friend))
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    //add friend

    //remove friend

}