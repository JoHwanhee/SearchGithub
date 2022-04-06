package com.hwanhee.search_github

import com.hwanhee.search_github.model.dto.RepositoryResponseDto
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubApi @Inject constructor(private val service: Service) {
    suspend fun search(word: String) = service.getRepositories()

    interface Service {
        // todo : 수정
        @GET("/search/repositories?q=test&page=1&per_page=20&order=desc")
        suspend fun getRepositories(): Response<RepositoryResponseDto>
    }
}