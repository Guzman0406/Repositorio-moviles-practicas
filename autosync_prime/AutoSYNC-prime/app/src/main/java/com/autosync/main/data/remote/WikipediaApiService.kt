package com.autosync.main.data.remote

import com.autosync.main.data.remote.model.WikiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApiService {
    @GET("w/api.php")
    suspend fun search(
        @Query("action") action: String = "query",
        @Query("list") list: String = "search",
        @Query("srsearch") query: String,
        @Query("format") format: String = "json",
        @Query("utf8") utf8: Int = 1
    ): WikiResponse
}
