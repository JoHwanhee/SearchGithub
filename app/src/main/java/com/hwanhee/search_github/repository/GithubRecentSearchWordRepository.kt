package com.hwanhee.search_github.repository

import com.hwanhee.search_github.GithubApi
import com.hwanhee.search_github.db.GithubTopicDao
import com.hwanhee.search_github.db.SearchWordDao
import com.hwanhee.search_github.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRecentSearchWordRepository @Inject constructor(
    private val searchWordDao: SearchWordDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

}