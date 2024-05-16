package com.example.linkitos.api

import com.example.linkitos.SHORTEN_LINK
import com.example.linkitos.api.requests.ShortenLinkRequest
import com.example.linkitos.api.responses.ShortenLinkResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LinksApiService {
    @POST(SHORTEN_LINK)
    suspend fun shortenLink(@Body shortenLinkRequest: ShortenLinkRequest): ShortenLinkResponse
}