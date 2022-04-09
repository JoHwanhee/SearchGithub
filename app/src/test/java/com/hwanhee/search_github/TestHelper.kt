package com.hwanhee.search_github

import com.hwanhee.search_github.di.BaseHeaderInterceptor
import com.hwanhee.search_github.di.IoDispatcher
import com.hwanhee.search_github.di.RetrofitModule
import com.hwanhee.search_github.model.entity.GithubRepositoryItemEntity
import com.hwanhee.search_github.model.entity.GithubRepositoryOwnerEntity
import kotlinx.coroutines.CoroutineDispatcher
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