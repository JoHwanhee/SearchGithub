package com.hwanhee.search_github.repository

import com.hwanhee.search_github.GithubApi
import com.hwanhee.search_github.db.GithubRepositoryItemDao
import com.hwanhee.search_github.db.GithubRepositoryOwnerDao
import com.hwanhee.search_github.db.GithubTopicDao
import com.hwanhee.search_github.di.IoDispatcher
import com.hwanhee.search_github.model.ResultWrapper
import com.hwanhee.search_github.model.dto.RepositoryResponseDto
import com.hwanhee.search_github.model.entity.GithubRepositoryItemAndOwner
import com.hwanhee.search_github.model.entity.GithubRepositoryItemEntity
import com.hwanhee.search_github.model.entity.GithubRepositoryOwnerEntity
import com.hwanhee.search_github.model.ui.RepositoryUIItems
import com.hwanhee.search_github.model.vo.RequestPage
import com.hwanhee.search_github.model.vo.SearchWord
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubSearchRepository @Inject constructor(
    private val api: GithubApi,
    private val topicDao: GithubTopicDao,
    private val itemDao: GithubRepositoryItemDao,
    private val ownerDao: GithubRepositoryOwnerDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private var _memCache: RepositoryUIItems? = null

    suspend fun searchRepository(word: SearchWord, page: RequestPage) = flow {
        // from memory
        getCache()?.let {
            emit(it)
        }

        // from database
        RepositoryUIItems.from(searchFromEntity(word)).let {
            if(it.items.count() > 0)
                emit(it)
        }

        // from network
        when (val res = searchFromNetwork(word, page)) {
            is ResultWrapper.Success -> {
                res.let {
                    val uiItem = RepositoryUIItems.from(it.value)
                    setCache(uiItem)
                    emit(uiItem)

                    // save cache
                    insertsToDatabase(it)
                }
            }
            is ResultWrapper.Error -> {
                emit(res.error)
            }
            else -> {
                emit(ResultWrapper.NetworkError)
            }
        }
    }
    .flowOn(ioDispatcher)

    private fun getCache()
            = _memCache

    private fun setCache(uiItem: RepositoryUIItems) {
        _memCache = uiItem
    }

    private suspend fun insertsToDatabase(res: ResultWrapper.Success<RepositoryResponseDto>) {
        val repositoryList = res.value.items.map { GithubRepositoryItemEntity.from(it) }
        val ownerList = res.value.items.filter { it.ownerDto != null }
            .map { GithubRepositoryOwnerEntity.from(it.ownerDto!!) }
        itemDao.insert(repositoryList)
        ownerDao.insert(ownerList)
    }

    private suspend fun searchFromEntity(word: SearchWord): List<GithubRepositoryItemAndOwner> {
        return if(word.isExtensionSearch)
            itemDao.searchByLanguage(word.keyword, word.language)
        else
            itemDao.search(word.keyword)
    }

    private suspend fun searchFromNetwork(word: SearchWord, page: RequestPage)
        = api.search(word, page)
}
