package com.example.linkitos.api.responses

import com.example.linkitos.api.dto.LinkDTO
import com.squareup.moshi.Json

class ShortenLinkResponse (
    val alias: String,
    @field:Json(name = "_links") val link: LinkDTO,
)