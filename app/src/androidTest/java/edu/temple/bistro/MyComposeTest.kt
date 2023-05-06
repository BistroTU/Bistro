package edu.temple.bistro

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import edu.temple.bistro.ui.theme.BistroTheme
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.SemanticsMatcher as SemanticsMatcher1

class MyComposeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity
    val email = "${(Math.random()*100).toInt().toString()}@bistro.gg"
    val password = "password1234"

    @Test
    fun signUpFlowTest() {
        // Start the app
        composeTestRule.onNodeWithText("First Name").performClick()
        composeTestRule.onNodeWithText("First Name").performTextInput("Robert")


        composeTestRule.onNodeWithText("Last Name").performClick()
        composeTestRule.onNodeWithText("Last Name").performTextInput("Bobert")


        composeTestRule.onNodeWithText("Email").performClick()
        composeTestRule.onNodeWithText("Email").performTextInput(email)

        composeTestRule.onNodeWithText("Password").performClick()
        composeTestRule.onNodeWithText("Password").performTextInput(password)

        composeTestRule.onNodeWithText("Sign Up").performClick()

        composeTestRule.onNodeWithText("Like this place").assertIsDisplayed()

//        composeTestRule.onNodeWithText("Dislike this place").assertIsDisplayed()
    }
}