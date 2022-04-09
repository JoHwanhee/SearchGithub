package com.hwanhee.search_github.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.hwanhee.search_github.di.DBModule
import com.hwanhee.search_github.model.entity.SearchWordEntity
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
import java.time.LocalDateTime

class SearchWordDaoTest {
    private val ioThreadSurrogate = newSingleThreadContext("IO thread")
    private lateinit var searchWordDao: SearchWordDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val provider = DBModule()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        searchWordDao = provider.provideSearchWordDao(db)

        Dispatchers.setMain(ioThreadSurrogate)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `기본_insert_와_get_이_잘되어야한다`() = runBlocking {
        val word = SearchWordEntity( 0,"tetris", "Assembly")
        searchWordDao.upsert(word)

        val words = searchWordDao.findSearchWordGreaterThen(word.createdAt)
        Assert.assertNotNull(words)
        Assert.assertTrue(words.count() == 1)
    }

    @Test
    fun `찾으려는_시간보다_더_큰_날짜를_요청한_경우_데이터는_없어야_한다`() = runBlocking {
        val word = SearchWordEntity( 0,"tetris", "Assembly")
        searchWordDao.insert(word)

        val date = LocalDateTime.of(2023, 1, 1, 1, 1)
        val words = searchWordDao.findSearchWordGreaterThen(date)
        Assert.assertNotNull(words)
        Assert.assertTrue(words.count() == 0)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        ioThreadSurrogate.close()
    }
}