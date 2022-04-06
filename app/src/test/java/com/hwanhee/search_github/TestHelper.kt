package com.hwanhee.search_github

import com.hwanhee.search_github.di.BaseHeaderInterceptor
import com.hwanhee.search_github.di.RetrofitModule
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets

interface TestHelper {
    fun provideGithubApi() : GithubApi
}

class TestHelperRealServer: TestHelper {
    override fun provideGithubApi(): GithubApi {
        val provider = RetrofitModule()
        val client = provider.provideAuthInterceptorOkHttpClient(
            provider.provideHttpLoggingInterceptor(),
            provider.provideBaseHeaderInterceptor(),
        )
        val retrofit = provider.provideRetrofit(client)
        val service = provider.provideGithubApiService(retrofit)
        return GithubApi(service)
    }
}

class TestHelperMock(private val baseUrl: HttpUrl): TestHelper {
    override fun provideGithubApi(): GithubApi {
        val provider = RetrofitModule()

        val client = provider.provideAuthInterceptorOkHttpClient(
            provider.provideHttpLoggingInterceptor(),
            provider.provideBaseHeaderInterceptor(),
        )
        val retrofit = provideRetrofit(client, baseUrl)
        val service = provider.provideGithubApiService(retrofit)
        return GithubApi(service)
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

internal fun MockWebServer.enqueueResponse(fileName: String, code: Int) {
    val inputStream = javaClass.classLoader?.getResourceAsStream("api-response/$fileName")

    val source = inputStream?.let { inputStream.source().buffer() }
    source?.let {
        enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(code)
                .setBody(source.readString(StandardCharsets.UTF_8))
        )
    }
}