package com.hwanhee.search_github.repository

import com.hwanhee.search_github.GithubApi
import com.hwanhee.search_github.db.SearchWordDao
import com.hwanhee.search_github.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubSearchRepository @Inject constructor(
    private val api: GithubApi,
    private val dao: SearchWordDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

}