package com.hwanhee.search_github.di

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseHeaderInterceptor @Inject constructor(
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var newRequest: Request = chain.request()
        newRequest = newRequest.newBuilder()
            .addHeader(
                "Accept",
                "application/vnd.github.v3+json"
            )
            .build()
        return chain.proceed(newRequest)
    }
}