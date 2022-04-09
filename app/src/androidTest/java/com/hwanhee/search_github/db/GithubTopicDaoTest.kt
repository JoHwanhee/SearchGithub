package com.hwanhee.search_github.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.hwanhee.search_github.di.DBModule
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

class GithubTopicDaoTest {
    private val ioThreadSurrogate = newSingleThreadContext("IO thread")
    private lateinit var ownersDao: GithubRepositoryOwnerDao
    private lateinit var itemDao: GithubRepositoryItemDao
    private lateinit var topicDao: GithubTopicDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val provider = DBModule()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        ownersDao = provider.provideOwnersDao(db)
        itemDao = provider.provideItemDao(db)
        topicDao = provider.provideTopicDao(db)
        Dispatchers.setMain(ioThreadSurrogate)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `기본_insert_와_get_이_잘되어야한다`() = runBlocking {
        val owners = createTestOwners(1)
        val item = createTestItem(owners.id)

        val topic = createTestTopic(item.id)
        val topic2 = createTestTopic(item.id)
        val topic3 = createTestTopic(item.id)
        val topic4 = createTestTopic(item.id)
        val topicArray = arrayListOf(topic, topic2, topic3, topic4)

        ownersDao.insert(owners)
        itemDao.insert(item)
        topicDao.insert(topicArray)

        val topics = topicDao.findGithubTopicsByRepositoryId(item.id)
        Assert.assertNotNull(topics)
        Assert.assertTrue(topics.count() == topicArray.count())
    }

    @Test
    fun `없는_번호로_조회할_때_리턴_0개`() = runBlocking {
        val owners = createTestOwners(1)
        val item = createTestItem(owners.id)

        val topic = createTestTopic(item.id)
        val topic2 = createTestTopic(item.id)
        val topic3 = createTestTopic(item.id)
        val topic4 = createTestTopic(item.id)
        val topicArray = arrayListOf(topic, topic2, topic3, topic4)

        ownersDao.insert(owners)
        itemDao.insert(item)
        topicDao.insert(topicArray)

        val topics = topicDao.findGithubTopicsByRepositoryId(99999)
        Assert.assertNotNull(topics)
        Assert.assertTrue(topics.count() == 0)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        ioThreadSurrogate.close()
    }
}