package com.example.linkitos.viewmodel

import com.example.linkitos.api.ApiResponseStatus
import com.example.linkitos.linklist.ShortenLinkUseCase
import com.example.linkitos.linklist.ui.LinkListViewModel
import com.example.linkitos.models.Link
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class LinkListViewModelTest {

    @get:Rule
    val linkitosDispatcherRule = LinkitosDispatcherRule()

    @Test
    fun shortenLinkStateUpdatesWhenSuccessStatus() {
        val shortLinkResponse = "https://this-is-a-beautiful-short-link/1234567"
        val fakeAlias = "26374585"

        class FakeShortenLinkUseCase : ShortenLinkUseCase {
            override fun invoke(link: String): Flow<ApiResponseStatus<Link>> = flowOf(
                ApiResponseStatus.Success(
                    Link(alias = fakeAlias, short = shortLinkResponse)
                )
            )
        }

        val viewModel = LinkListViewModel(
            shortenLinkUseCase = FakeShortenLinkUseCase()
        )

        // At first we do not have links in the state and status is None
        assertEquals(0, viewModel.state.value.links.size)
        assert(viewModel.state.value.status is ApiResponseStatus.None)

        // We are shortening 2 links
        val expectedNumberOfLinks = 2
        viewModel.shortenLink("https://www.someLink1.com")
        viewModel.shortenLink("https://www.someLink2.com")

        assert(viewModel.state.value.status is ApiResponseStatus.Success)
        assertEquals(expectedNumberOfLinks, viewModel.state.value.links.size)
        assertEquals(shortLinkResponse, viewModel.state.value.links[0].short)
        assertEquals(fakeAlias, viewModel.state.value.links[0].alias)
    }

    @Test
    fun errorStatusUpdatesCorrectly() {
        val fakeErrorMessage = "Oops, there was an error!!"

        class FakeShortenLinkUseCase : ShortenLinkUseCase {
            override fun invoke(link: String): Flow<ApiResponseStatus<Link>> = flowOf(
                ApiResponseStatus.Error(
                    message = fakeErrorMessage
                )
            )
        }

        val viewModel = LinkListViewModel(
            shortenLinkUseCase = FakeShortenLinkUseCase()
        )

        // At first we have no links and status is None
        assertEquals(0, viewModel.state.value.links.size)
        assert(viewModel.state.value.status is ApiResponseStatus.None)

        // We are trying to shorten 1 link
        viewModel.shortenLink("https://www.someLink.com")

        // There was an error (caused by the fake repository)
        assert(viewModel.state.value.status is ApiResponseStatus.Error)

        // Error message should be present
        assertEquals(
            fakeErrorMessage,
            (viewModel.state.value.status as ApiResponseStatus.Error).message
        )
        // No links were added
        assertEquals(0, viewModel.state.value.links.size)
    }

    @Test
    fun resetStatusUpdatesCorrectly() {
        val fakeErrorMessage = "Oops, there was an error!!"

        class FakeShortenLinkUseCase : ShortenLinkUseCase {
            override fun invoke(link: String): Flow<ApiResponseStatus<Link>> = flowOf(
                ApiResponseStatus.Error(
                    message = fakeErrorMessage
                )
            )
        }

        val viewModel = LinkListViewModel(
            shortenLinkUseCase = FakeShortenLinkUseCase()
        )

        // At first status is None
        assert(viewModel.state.value.status is ApiResponseStatus.None)

        // We are trying to shorten 1 link and causing an error
        viewModel.shortenLink("https://www.someLink.com")

        // There was an error (caused by the fake repository)
        assert(viewModel.state.value.status is ApiResponseStatus.Error)

        // Calling method to reset status
        viewModel.resetApiResponseStatus()

        // status is None again
        assert(viewModel.state.value.status is ApiResponseStatus.None)
    }
}