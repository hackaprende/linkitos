package com.example.linkitos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.linkitos.api.ApiResponseStatus
import com.example.linkitos.linklist.ShortenLinkUseCase
import com.example.linkitos.linklist.ui.LinkListScreen
import com.example.linkitos.linklist.ui.LinkListViewModel
import com.example.linkitos.models.Link
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class LinkListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun progressBarShowsWhenLoadingState() {
        class FakeShortenLinkUseCase : ShortenLinkUseCase {
            override fun invoke(link: String): Flow<ApiResponseStatus<Link>> = flowOf(
                ApiResponseStatus.Loading()
            )
        }

        val viewModel = LinkListViewModel(
            shortenLinkUseCase = FakeShortenLinkUseCase()
        )

        viewModel.shortenLink("https://someLink.com")

        composeTestRule.setContent {
            LinkListScreen(
                onLinkClicked = {},
                linkListViewModel = viewModel,
            )
        }

        /***
         *  Check that Progressbar is present by using the semantics tag from [com.example.linkitos.LoadingWheel]
         */
        composeTestRule.onNodeWithTag(testTag = "loading-wheel" ).assertIsDisplayed()
    }

    @Test
    fun errorDialogShowsWithCorrectMessageIfThereIsAnErrorShorteningLink() {
        val fakeError = "Wow, seems like an error :O"
        class FakeShortenLinkUseCase : ShortenLinkUseCase {
            override fun invoke(link: String): Flow<ApiResponseStatus<Link>> = flowOf(
                ApiResponseStatus.Error(message = fakeError)
            )
        }

        val viewModel = LinkListViewModel(
            shortenLinkUseCase = FakeShortenLinkUseCase()
        )

        composeTestRule.setContent {
            LinkListScreen(
                onLinkClicked = {},
                linkListViewModel = viewModel,
            )
        }

        composeTestRule.onNodeWithTag(testTag = "link-field").performTextInput("https://www.myAwesomeLink.com/")
        composeTestRule.onNodeWithTag(testTag = "send-link-button").performClick()

        /***
         *  Check that Dialog is present by using the semantics tag from [com.example.linkitos.ErrorDialog]
         */
        composeTestRule.onNodeWithTag(testTag = "error-dialog" ).assertIsDisplayed()
        // Dialog is present, now check that message displayed is correct
        composeTestRule.onNodeWithText(text = fakeError ).assertIsDisplayed()
    }

    /***
     * We are adding a link and checking if the link is displayed after adding it
     */
    @Test
    fun addedLinkIsDisplayed() {
        val fakeLink = "https://www.anakinIsYourFather.com"

        class FakeShortenLinkUseCase : ShortenLinkUseCase {
            override fun invoke(link: String): Flow<ApiResponseStatus<Link>> = flowOf(
                ApiResponseStatus.Success(data = Link(alias = "", short = fakeLink))
            )
        }

        val viewModel = LinkListViewModel(
            shortenLinkUseCase = FakeShortenLinkUseCase()
        )

        composeTestRule.setContent {
            LinkListScreen(
                onLinkClicked = {},
                linkListViewModel = viewModel,
            )
        }

        /***
         * First, the link is not there, we are checking this by using a testTag on each list item
         * see [com.example.linkitos.linklist.ui.LinkListScreen] LinkList Composable to check how
         * this is implemented.
         */
        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "link-${fakeLink}").assertDoesNotExist()

        // Write and send the link
        composeTestRule.onNodeWithTag(testTag = "link-field").performTextInput("https://www.myAwesomeLink.com/")
        composeTestRule.onNodeWithTag(testTag = "send-link-button").performClick()

        // Now the link is there!
        composeTestRule.onNodeWithTag(useUnmergedTree = true, testTag = "link-${fakeLink}").assertIsDisplayed()
    }

    @Test
    fun sendLinkButtonNotEnabledIfTextEmpty() {
        class FakeShortenLinkUseCase : ShortenLinkUseCase {
            override fun invoke(link: String): Flow<ApiResponseStatus<Link>> = flowOf(
                ApiResponseStatus.Loading()
            )
        }

        val viewModel = LinkListViewModel(
            shortenLinkUseCase = FakeShortenLinkUseCase()
        )

        composeTestRule.setContent {
            LinkListScreen(
                onLinkClicked = {},
                linkListViewModel = viewModel,
            )
        }

        // The ProgressBar is not present since we haven't tried to shorten a link
        composeTestRule.onNodeWithTag(testTag = "loading-wheel" ).assertDoesNotExist()

        // Try to send the link but text is empty
        composeTestRule.onNodeWithTag(testTag = "send-link-button").performClick()

        // ProgressBar is still not present
        composeTestRule.onNodeWithTag(testTag = "loading-wheel" ).assertDoesNotExist()

        // Now write the link and try to send it again
        composeTestRule.onNodeWithTag(testTag = "link-field").performTextInput("https://www.myAwesomeLink.com/")
        composeTestRule.onNodeWithTag(testTag = "send-link-button").performClick()

        // Now ProgressBar is there!
        composeTestRule.onNodeWithTag(testTag = "loading-wheel" ).assertIsDisplayed()
    }

    @Test
    fun errorDialogShowsIfUrlIsNotValid() {
        class FakeShortenLinkUseCase : ShortenLinkUseCase {
            override fun invoke(link: String): Flow<ApiResponseStatus<Link>> = flowOf(
                ApiResponseStatus.Loading()
            )
        }

        val viewModel = LinkListViewModel(
            shortenLinkUseCase = FakeShortenLinkUseCase()
        )

        composeTestRule.setContent {
            LinkListScreen(
                onLinkClicked = {},
                linkListViewModel = viewModel,
            )
        }

        // Dialog is not there
        composeTestRule.onNodeWithTag(testTag = "error-dialog" ).assertDoesNotExist()
        composeTestRule.onNodeWithTag(testTag = "loading-wheel" ).assertDoesNotExist()

        // Adding an invalid link
        composeTestRule.onNodeWithTag(testTag = "link-field").performTextInput("thisIsNotAnUrl")
        composeTestRule.onNodeWithTag(testTag = "send-link-button").performClick()
        // Dialog shows, clicking ok dismisses de dialog
        composeTestRule.onNodeWithTag(testTag = "error-dialog" ).assertIsDisplayed()
        composeTestRule.onNodeWithTag(testTag = "error-dialog-button").performClick()
        // Dialog was dismissed
        composeTestRule.onNodeWithTag(testTag = "error-dialog" ).assertDoesNotExist()

        // Clear the edit text and this time we add a valid link
        composeTestRule.onNodeWithTag(testTag = "link-field").performTextClearance()
        composeTestRule.onNodeWithTag(testTag = "link-field").performTextInput("https://www.myPrettyLink.com")
        composeTestRule.onNodeWithTag(testTag = "send-link-button").performClick()

        // This time, the request is made and we see the ProgressBar
        composeTestRule.onNodeWithTag(testTag = "loading-wheel" ).assertIsDisplayed()
    }
}