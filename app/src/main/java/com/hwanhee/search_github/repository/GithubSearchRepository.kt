package com.hwanhee.search_github.repository

import com.hwanhee.search_github.GithubApi
import com.hwanhee.search_github.db.GithubRepositoryItemDao
import com.hwanhee.search_github.db.GithubRepositoryOwnerDao
import com.hwanhee.search_github.db.GithubTopicDao
import com.hwanhee.search_github.di.IoDispatcher
import com.hwanhee.search_github.model.ResultWrapper
import com.hwanhee.search_github.model.dto.ItemDto
import com.hwanhee.search_github.model.dto.RepositoryResponseDto
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
        when (val dataFromNetwork = searchFromNetwork(word, page)) {
            is ResultWrapper.Success -> {
                dataFromNetwork.let {
                    emit(RepositoryUIItems.from(it.value))
                    insertsToDatabase(it)
                }
            }
            else -> {
                val dataFromDb = searchFromDatabase(word)
                emit(RepositoryUIItems.from(dataFromDb))
                emit(ResultWrapper.NetworkError)
            }
        }
    }
    .flowOn(ioDispatcher)

    private suspend fun insertsToDatabase(res: ResultWrapper.Success<RepositoryResponseDto>) {
        val items = res.value.items.filterByNotNullOwners()
        if (items.isEmpty())
            return

        ownerDao.upsert(items.mapOwnerEntities())
        itemDao.upsert(items.mapRepositoryEntities())
        topicDao.upsert(items.mapTopicEntities())
    }

    private suspend fun searchFromDatabase(word: SearchWord) = itemDao.search(word.toLikeQueryString())

    private suspend fun searchFromNetwork(word: SearchWord, page: RequestPage) = api.search(word, page)

    /** Helpers **/
    private fun List<ItemDto>.filterByNotNullOwners()
    = this.filter { it.ownerDto != null }

    private fun List<ItemDto>.mapOwnerEntities()
    = this.filter { it.ownerDto != null }
          .map { GithubRepositoryOwnerEntity.from(it.ownerDto!!) }

    private fun List<ItemDto>.mapRepositoryEntities()
    = this.map { GithubRepositoryItemEntity.from(it) }

    private fun List<ItemDto>.mapTopicEntities()
    = this.filter { itemDto -> itemDto.id != null }
          .flatMap { itemDto ->
                itemDto.topics.map { GithubTopicEntity.from(itemDto.id!!, it) }
          }
}
