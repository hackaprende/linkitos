package com.example.linkitos.api

import com.example.linkitos.models.Link
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun testFirstNetworkEmissionIsLoading() = runTest(testDispatcher.scheduler) {
        val network = NetworkImpl(testDispatcher)

        val networkCallFlow = network.makeNetworkCall {
            throw UnknownHostException()
        }

        val result = networkCallFlow.first()

        assert(result is ApiResponseStatus.Loading)
    }

    @Test
    fun testExceptionsAreHandledWithCorrectMessage() = runTest(testDispatcher.scheduler) {
        val network = NetworkImpl(testDispatcher)

        val networkCallFlow = network.makeNetworkCall {
            throw UnknownHostException()
        }

        // The first value that network emits is loading, but we need the second one
        val result = networkCallFlow.drop(1).first()

        assert(result is ApiResponseStatus.Error)
        assertEquals(NetworkImpl.MESSAGE_UNKNOWN_HOST_EXCEPTION, (result as ApiResponseStatus.Error).message)
    }

    @Test
    fun testIfCodeIsGoodReturnSuccess() = runTest(testDispatcher.scheduler) {
        val network = NetworkImpl(testDispatcher)
        val fakeLink = "https://www.aLinkToThePast.com"
        val networkCallFlow = network.makeNetworkCall {
            // Just do something good, return a link
            Link(alias = "", short = fakeLink)
        }

        // The first value that network emits is loading, but we need the second one
        val result = networkCallFlow.drop(1).first()

        assert(result is ApiResponseStatus.Success)
        assertEquals(fakeLink, (result as ApiResponseStatus.Success).data.short)
    }

    @Test
    fun returnErrorIfResponseSuccessButDataNull() = runTest(testDispatcher.scheduler) {
        val network = NetworkImpl(testDispatcher)
        val networkCallFlow = network.makeNetworkCall {
            // Everything is ok but we return null
            null
        }

        // The first value that network emits is loading, but we need the second one
        val result = networkCallFlow.drop(1).first()

        assert(result is ApiResponseStatus.Error)
    }
}