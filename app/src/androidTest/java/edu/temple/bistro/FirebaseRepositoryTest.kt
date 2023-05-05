package edu.temple.bistro

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.database.FirebaseDatabase
import edu.temple.bistro.data.firebase.*
import edu.temple.bistro.data.repository.FirebaseRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FirebaseRepositoryTest {

    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var firebaseDatabase: FirebaseDatabase

    private val user1 =
        FirebaseUser("Test", "1", "test1@test.com")
    private val user2 =
        FirebaseUser("Test", "2", "test2@test.com")
    private val group1 = FirebaseGroup("group1", mutableListOf("Group 1"))
    private val place1 = FirebasePlace(
        "place1",
        "Place 1",
        1,
        "www.google.com"
    )
    private val category1 = FirebaseCategory("category1", "Category 1")
    private val friend1 = FirebaseFriend(user2.username!!, "test2@test.com")

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseDatabase.useEmulator("127.0.0.1", 9000)
        firebaseDatabase.setPersistenceEnabled(false)
        firebaseRepository = FirebaseRepository(firebaseDatabase, context)

        // Set up initial test data in the emulator
        firebaseDatabase.getReference("users").child(FirebaseRepository.keyStr(user1.username!!))
            .setValue(user1)
        firebaseDatabase.getReference("users").child(FirebaseRepository.keyStr(user2.username!!))
            .setValue(user2)
        firebaseDatabase.getReference("groups").child(group1.id!!).setValue(group1)
        firebaseDatabase.getReference("places").child(place1.id!!).setValue(place1)
        firebaseDatabase.getReference("categories").child(category1.alias!!)
            .setValue(category1)
    }

    @Test
    fun testCreateGroup() {
        firebaseDatabase.reference.setValue(null)
        firebaseRepository.createGroup(user1)
        assertTrue(firebaseDatabase.getReference("groups").key != null)
    }

    @Test
    fun testGetCommonCategories() {
        user1.liked_categories = mutableMapOf("cat1" to FirebaseCategory("cat1", "Category 1"))
        user2.liked_categories = mutableMapOf("cat2" to FirebaseCategory("cat2", "Category 2"))
        val result = firebaseRepository.getCommonCategories(listOf("user1", "user2"))
        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun testGetCommonPlaces() {
        val place1 = FirebasePlace("place1", "Place 1", 1)
        val place2 = FirebasePlace("place2", "Place 2", 2)
        val place3 = FirebasePlace("place3", "Place 3", 3)
        user1.liked_places = mutableMapOf("place1" to place1, "place2" to place2)
        user2.liked_places = mutableMapOf("place3" to place3)
        val result = firebaseRepository.getCommonPlaces(listOf("user1", "user2"))
        assertEquals(emptyMap<String, FirebasePlace>(), result)
    }

    @Test
    fun testGenerateGroupID() {
        val result = firebaseRepository.generateGroupID()
        assertTrue(result.matches(Regex("[A-Z][0-9]{4}")))
    }

}