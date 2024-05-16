package com.example.linkitos.repository

import com.example.linkitos.api.ApiResponseStatus
import com.example.linkitos.api.LinksApiService
import com.example.linkitos.api.Network
import com.example.linkitos.api.dto.LinkDTO
import com.example.linkitos.api.requests.ShortenLinkRequest
import com.example.linkitos.api.responses.ShortenLinkResponse
import com.example.linkitos.linklist.LinksRepositoryImpl
import com.example.linkitos.models.Link
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LinksRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun shortenLinkSuccess() = runTest(testDispatcher.scheduler) {
        val fakeLink = "https://www.myAwesomeShortenedLink.com/repoTesting/1234"
        val fakeAlias = "1234567"

        class FakeLinksApiService : LinksApiService {
            override suspend fun shortenLink(shortenLinkRequest: ShortenLinkRequest): ShortenLinkResponse {
                return ShortenLinkResponse(
                    alias = "",
                    link = LinkDTO(short = fakeLink)
                )
            }
        }

        class FakeNetwork : Network {
            override fun <T> makeNetworkCall(api: suspend () -> T):
                    Flow<ApiResponseStatus<T>> = flow {
                // Emitting a Success state
                emit(apiCall())
            }

            @Suppress("UNCHECKED_CAST")
            private suspend fun <T> apiCall(): ApiResponseStatus<T> = withContext(context = testDispatcher) {
                val link = Link(alias = fakeAlias, short = fakeLink)
                return@withContext  ApiResponseStatus.Success(data = link) as ApiResponseStatus<T>
            }
        }

        val linksRepository = LinksRepositoryImpl(FakeLinksApiService(), FakeNetwork())
        val shortenLink = linksRepository.shortenLink("https://www.myLinkToShorten.com")

        // Value emitted has Success status and has the {fakeLink} link
        val first = shortenLink.first()
        assert(first is ApiResponseStatus.Success)
        assertEquals(fakeLink, (first as ApiResponseStatus.Success).data.short)
        assertEquals(fakeAlias, first.data.alias)
    }

    @Test
    fun shortenLinkErrorWithException() = runTest(testDispatcher.scheduler) {
        val fakeLink = "https://www.myAwesomeShortenedLink.com/repoTesting/1234"
        val fakeError = "There was a big error"

        class FakeLinksApiService : LinksApiService {
            override suspend fun shortenLink(shortenLinkRequest: ShortenLinkRequest): ShortenLinkResponse {
                return ShortenLinkResponse(
                    alias = "",
                    link = LinkDTO(short = fakeLink)
                )
            }
        }

        class FakeNetwork : Network {
            override fun <T> makeNetworkCall(api: suspend () -> T):
                    Flow<ApiResponseStatus<T>> = flow {
                // Emitting two states, a loading and an error
                emit(ApiResponseStatus.Loading())
                emit(apiCall())
            }

            @Suppress("UNCHECKED_CAST")
            private suspend fun <T> apiCall(): ApiResponseStatus<T> = withContext(context = testDispatcher) {
                return@withContext  ApiResponseStatus.Error<Link>(message = fakeError) as ApiResponseStatus<T>
            }
        }

        // Creating repository to test
        val linksRepository = LinksRepositoryImpl(FakeLinksApiService(), FakeNetwork())

        // Calling method to test
        val shortenLink = linksRepository.shortenLink("https://www.myLinkToShorten.com")

        // First emitted value is a Loading status
        val first = shortenLink.first()
        assert(first is ApiResponseStatus.Loading)
        // Second emitted value is an Error status with {fakeError} message
        val second = shortenLink.drop(1).first()
        assert(second is ApiResponseStatus.Error)
        assertEquals(fakeError, (second as ApiResponseStatus.Error).message)
    }
}