package com.hwanhee.search_github.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.hwanhee.search_github.TestHelperRealServer
import com.hwanhee.search_github.db.AppDatabase
import com.hwanhee.search_github.di.DBModule
import com.hwanhee.search_github.model.ErrorResponse
import com.hwanhee.search_github.model.ResultWrapper
import com.hwanhee.search_github.model.ui.RepositoryUIItems
import com.hwanhee.search_github.model.vo.RequestPage
import com.hwanhee.search_github.model.vo.SearchWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okio.IOException
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GithubSearchedRepositoryTest {
    private val ioThreadSurrogate = newSingleThreadContext("IO thread")
    private lateinit var repository: GithubSearchRepository
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val dbProvider = DBModule()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        val ownersDao = dbProvider.provideOwnersDao(db)
        val itemDao = dbProvider.provideItemDao(db)
        val topicDao = dbProvider.provideTopicDao(db)

        // android test 에서는 real server 테스트를 진행
        val helper = TestHelperRealServer(dispatcher = ioThreadSurrogate)
        repository = GithubSearchRepository(
            helper.provideGithubApi(),
            topicDao,
            itemDao,
            ownersDao,
            ioThreadSurrogate
        )

        Dispatchers.setMain(ioThreadSurrogate)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `데이터_수집_성공_되어야_한다`() = runBlocking {
        repository.searchRepository(SearchWord("tetris"), RequestPage(1, 20, 20)).collect {
            if(it is RepositoryUIItems) {
                Assert.assertNotNull(it.items)
            }
        }
    }

    @Test
    fun `요청이_20개면_20개_리턴_되어야_한다`() = runBlocking {
        val perPage = 20

        repository.searchRepository(SearchWord("tetris"), RequestPage(1, perPage, perPage)).collect {
            if(it is RepositoryUIItems) {
                Assert.assertNotNull(it.items)
                Assert.assertTrue(it.items.count() == perPage)
            }
        }
    }

    @Test
    fun `요청이_7개면_7개_리턴_되어야_한다`() = runBlocking {
        val perPage = 7

        repository.searchRepository(SearchWord("tetris"), RequestPage(1, perPage, perPage)).collect {
            if(it is RepositoryUIItems) {
                Assert.assertNotNull(it.items)
                Assert.assertTrue(it.items.count() == perPage)
            }
        }
    }

    @Test
    fun `Assembly_언어_요청하면_Assembly만_있어야_한다`() = runBlocking {
        val perPage = 7

        repository.searchRepository(SearchWord("tetris", "Assembly"), RequestPage(1, perPage, perPage)).collect { it ->
            if(it is RepositoryUIItems) {
                Assert.assertNotNull(it.items)
                Assert.assertTrue(it.items.count() == perPage)

                Assert.assertTrue(
                    it.items.filter { it.language == "Assembly" }
                        .count()
                    == perPage
                )
            }

        }
    }

    @Test
    fun `공백문자를_검색하면_파싱된_에러가_리스폰스_되어야한다`() = runBlocking {
        val perPage = 7

        repository.searchRepository(SearchWord(""), RequestPage(1, perPage, perPage)).collect { it ->
            if(it is ResultWrapper.Error) {
                Assert.assertNotNull(it)

                Assert.assertTrue(it.code == 422)
                // 공통 에러로 사용
                Assert.assertTrue(it.error.title == ErrorResponse.empty().title)
                Assert.assertTrue(it.error.message == ErrorResponse.empty().message)
            }
        }
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
        ioThreadSurrogate.close()
    }
}