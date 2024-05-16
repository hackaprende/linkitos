package com.example.linkitos.linklist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkitos.api.ApiResponseStatus
import com.example.linkitos.linklist.ShortenLinkUseCase
import com.example.linkitos.models.Link
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LinkListState(
    val status: ApiResponseStatus<Link>,
    val links: List<Link>,
    val latestLink: Link?,
)

val initialState = LinkListState(
    status = ApiResponseStatus.None(),
    links = listOf(),
    latestLink = null,
)

@HiltViewModel
class LinkListViewModel @Inject constructor(
    private val shortenLinkUseCase: ShortenLinkUseCase,
): ViewModel() {
    private val mutableStateFlow = MutableStateFlow(initialState)
    val state = mutableStateFlow.asStateFlow()

    fun resetApiResponseStatus() {
        viewModelScope.launch {
            mutableStateFlow.emit(
                state.value.copy(
                    status = ApiResponseStatus.None(),
                )
            )
        }
    }

    fun shortenLink(link: String) {
        shortenLinkUseCase(link)
            .onEach(::handleShortenLinkResponse)
            .launchIn(viewModelScope)
    }

    private suspend fun handleShortenLinkResponse(responseStatus: ApiResponseStatus<Link>) {
        if (responseStatus is ApiResponseStatus.Success) {
            val newLink = responseStatus.data
            val newLinksList = state.value.links.toMutableList()
            newLinksList.add(0, newLink)
            val newState = state.value.copy(
                status = responseStatus,
                links = newLinksList,
                latestLink = newLink,
            )
            mutableStateFlow.emit(newState)
        } else {
            val newState = state.value.copy(
                status = responseStatus,
            )
            mutableStateFlow.emit(newState)
        }
    }
}