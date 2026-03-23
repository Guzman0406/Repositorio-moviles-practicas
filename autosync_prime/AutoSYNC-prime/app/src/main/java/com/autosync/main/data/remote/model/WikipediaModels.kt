package com.autosync.main.data.remote.model

import com.google.gson.annotations.SerializedName

data class WikiResponse(
    val query: WikiQuery?
)

data class WikiQuery(
    val search: List<WikiSearchResult>?
)

data class WikiSearchResult(
    val title: String,
    val snippet: String
)
