package com.example.linkitos.linklist

import com.example.linkitos.api.ApiResponseStatus
import com.example.linkitos.api.LinksApiService
import com.example.linkitos.api.Network
import com.example.linkitos.api.requests.ShortenLinkRequest
import com.example.linkitos.models.Link
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LinksRepository {
    fun shortenLink(link: String): Flow<ApiResponseStatus<Link>>
}

class LinksRepositoryImpl @Inject constructor(
    private val linksApiService: LinksApiService,
    private val network: Network,
) : LinksRepository {
    override fun shortenLink(link: String) =
        network.makeNetworkCall {
            val response = linksApiService.shortenLink(ShortenLinkRequest(link))
            Link(response.alias, response.link.short)
        }
}