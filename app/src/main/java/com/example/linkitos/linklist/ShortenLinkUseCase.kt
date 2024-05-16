package com.example.linkitos.linklist

import com.example.linkitos.api.ApiResponseStatus
import com.example.linkitos.models.Link
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ShortenLinkUseCase {
    operator fun invoke(link: String): Flow<ApiResponseStatus<Link>>
}

class ShortenLinkUseCaseImpl @Inject constructor(private val linksRepository: LinksRepository) : ShortenLinkUseCase {

    override operator fun invoke(link: String) =
        linksRepository.shortenLink(link)
}