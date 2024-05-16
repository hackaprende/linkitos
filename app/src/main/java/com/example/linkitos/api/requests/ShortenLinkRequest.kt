package com.example.linkitos.api.requests

import com.squareup.moshi.Json

class ShortenLinkRequest(
    @field:Json(name = "url") val link: String,
)