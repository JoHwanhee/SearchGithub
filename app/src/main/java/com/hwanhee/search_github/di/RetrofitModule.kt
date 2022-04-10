package com.hwanhee.search_github.di

import com.hwanhee.search_github.GithubApi
import com.hwanhee.search_github.base.GITHUB_API_HOST
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class RetrofitModule {
    @Provides
    @Singleton
    fun provideAuthInterceptorOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        baseHeaderInterceptor: BaseHeaderInterceptor)
    = OkHttpClient
        .Builder()
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(baseHeaderInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit
    = Retrofit
        .Builder()
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(GITHUB_API_HOST)
        .build()

    @Provides
    @Singleton
    fun provideGithubApiService(
        retrofit: Retrofit
    ): GithubApi.Service
            = retrofit.create(GithubApi.Service::class.java)

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor()
    = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun provideBaseHeaderInterceptor()
            = BaseHeaderInterceptor()
}