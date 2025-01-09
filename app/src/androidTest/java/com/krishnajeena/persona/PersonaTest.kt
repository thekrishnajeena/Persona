package com.krishnajeena.persona

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import com.krishnajeena.persona.ui_layer.PersonaApp
import org.junit.Rule
import org.junit.Test

class PersonaTest {

    @get:Rule
    val rule = createComposeRule()

    @get:Rule
    val aRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun dummyTestPersona(){
        rule.setContent {
           PersonaApp(sharedViewModel)
        }

        rule.onRoot(useUnmergedTree = true).printToLog("Clicks")
        rule.onNode(hasText("Notes")).performClick().assertExists()
        rule.onNode(hasText("Music")).performClick().assertExists()

        Thread.sleep(5000)


    }
}