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
import com.hwanhee.search_github.model.entity.GithubTopicEntity
import com.hwanhee.search_github.model.ui.RepositoryUIItems
import com.hwanhee.search_github.model.vo.RequestPage
import com.hwanhee.search_github.model.vo.SearchWord
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
    suspend fun searchRepository(word: SearchWord, page: RequestPage) = flow {
        when (val res = searchFromNetwork(word, page)) {
            is ResultWrapper.Success -> {
                res.let {
                    val uiItem = RepositoryUIItems.from(it.value)
                    emit(uiItem)

                    insertsToDatabase(it)
                }
            }
            else -> {
                // 네트워크 실패 시, 디비에서 조회한다.
                val dataFromDb = searchFromDatabase(word)
                RepositoryUIItems.from(dataFromDb).let {
                    if(it.items.isNotEmpty())
                        emit(it)
                }

                emit(ResultWrapper.NetworkError)
            }
        }
    }
    .flowOn(ioDispatcher)

    private suspend fun insertsToDatabase(res: ResultWrapper.Success<RepositoryResponseDto>) {
        if(res.value.items.isEmpty())
            return

        val items = res.value.items.filter { it.ownerDto != null }

        val repositoryList = items.map { GithubRepositoryItemEntity.from(it) }
        itemDao.upsert(repositoryList)

        val ownerList = items.map { GithubRepositoryOwnerEntity.from(it.ownerDto!!) }
        ownerDao.upsert(ownerList)

        items.map {
            it.id to it.topics
        }
        .forEach { pair ->
            pair.second.forEach { topic ->
                pair.first?.let { repositoryId ->
                    topicDao.upsert(GithubTopicEntity.from(repositoryId, topic))
                }
            }
        }
    }

    private suspend fun searchFromDatabase(word: SearchWord): List<GithubRepositoryItemAndOwner> {
        return if(word.isExtensionSearch)
            itemDao.searchByLanguage("%${word.keyword}%", word.language)
        else
            itemDao.search("%${word.keyword}%")
    }

    private suspend fun searchFromNetwork(word: SearchWord, page: RequestPage)
        = api.search(word, page)
}
