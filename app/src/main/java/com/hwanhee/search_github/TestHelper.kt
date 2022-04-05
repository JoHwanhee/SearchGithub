package com.hwanhee.search_github

import android.content.Context
import com.hwanhee.search_github.base.GITHUB_API_HOST
import com.hwanhee.search_github.di.BaseHeaderInterceptor
import com.hwanhee.search_github.di.RetrofitModule
import dagger.Provides
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

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