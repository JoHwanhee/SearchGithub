package com.hwanhee.search_github

import com.hwanhee.search_github.di.IoDispatcher
import com.hwanhee.search_github.model.dto.RepositoryResponseDto
import com.hwanhee.search_github.model.safeApiCall
import com.hwanhee.search_github.model.vo.RequestPage
import com.hwanhee.search_github.model.vo.SearchWord
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubApi @Inject constructor (
    private val service: Service,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun search(
        query: SearchWord,
        page: RequestPage,
    ) = search(
        query.toString(),
        page.page,
        page.perPage
    )

    suspend fun search(
        query: SearchWord,
        page: Int,
        perPage: Int
    ) = search(
        query.toString(),
        page,
        perPage
    )

    suspend fun search(
        query: String,
        page: Int,
        perPage: Int
    ) = safeApiCall(
        ioDispatcher
    ) {
        service.getRepositories(
            query,
            page,
            size = if (perPage < 1) 20 else perPage
        )
    }

    interface Service {
        @GET("/search/repositories")
        suspend fun getRepositories(
            @Query(value = "q") query: String,
            @Query(value = "page") page: Int,
            @Query(value = "order") order: String = "desc",
            @Query(value = "per_page") size: Int = 20,
        ): RepositoryResponseDto
    }
}