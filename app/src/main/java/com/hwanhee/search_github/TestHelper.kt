package com.hwanhee.search_github

import com.hwanhee.search_github.di.RetrofitModule
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface TestHelper {
    fun provideGithubApi() : GithubApi
}

class TestHelperRealServer(
    private val dispatcher: CoroutineDispatcher,
): TestHelper {
    override fun provideGithubApi(): GithubApi {
        val provider = RetrofitModule()
        val client = provider.provideAuthInterceptorOkHttpClient(
            provider.provideHttpLoggingInterceptor(),
            provider.provideBaseHeaderInterceptor(),
        )
        val retrofit = provider.provideRetrofit(client)
        val service = provider.provideGithubApiService(retrofit)
        return GithubApi(service, dispatcher)
    }
}

class TestHelperMock(
    private val dispatcher: CoroutineDispatcher,
    private val baseUrl: HttpUrl
): TestHelper {
    override fun provideGithubApi(): GithubApi {
        val provider = RetrofitModule()

        val client = provider.provideAuthInterceptorOkHttpClient(
            provider.provideHttpLoggingInterceptor(),
            provider.provideBaseHeaderInterceptor(),
        )
        val retrofit = provideRetrofit(client, baseUrl)
        val service = provider.provideGithubApiService(retrofit)
        return GithubApi(service, dispatcher)
    }

    private fun provideRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: HttpUrl
    ): Retrofit
            = Retrofit
        .Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()
}