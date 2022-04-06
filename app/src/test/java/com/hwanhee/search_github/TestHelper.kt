package com.hwanhee.search_github

import com.hwanhee.search_github.di.BaseHeaderInterceptor
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

class TestHelper {
    companion object {
        fun provideGithubApi(baseUrl: HttpUrl): GithubApi {
            val provider = TestHelper()
            val client = provider.provideAuthInterceptorOkHttpClient(
                provider.provideHttpLoggingInterceptor(),
                provider.provideBaseHeaderInterceptor(),
            )
            val retrofit = provider.provideRetrofit(client, baseUrl)
            val service = provider.provideGithubApiService(retrofit)
            return GithubApi(service)
        }
    }

    fun provideAuthInterceptorOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        baseHeaderInterceptor: BaseHeaderInterceptor
    )
            = OkHttpClient
        .Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(baseHeaderInterceptor)
        .build()

    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: HttpUrl
    ): Retrofit
            = Retrofit
        .Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    fun provideGithubApiService(
        retrofit: Retrofit
    ): GithubApi.Service
            = retrofit.create(GithubApi.Service::class.java)

    fun provideHttpLoggingInterceptor()
            = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    fun provideBaseHeaderInterceptor()
            = BaseHeaderInterceptor()
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