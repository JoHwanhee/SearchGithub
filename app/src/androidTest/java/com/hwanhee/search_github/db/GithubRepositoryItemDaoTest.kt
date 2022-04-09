package com.hwanhee.search_github.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.hwanhee.search_github.di.DBModule
import com.hwanhee.search_github.model.entity.GithubRepositoryItemEntity
import com.hwanhee.search_github.model.entity.GithubRepositoryOwnerEntity
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

class GithubRepositoryItemDaoTest {
    private val ioThreadSurrogate = newSingleThreadContext("IO thread")
    private lateinit var ownersDao: GithubRepositoryOwnerDao
    private lateinit var itemDao: GithubRepositoryItemDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val provider = DBModule()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        ownersDao = provider.provideOwnersDao(db)
        itemDao = provider.provideItemDao(db)
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
        ownersDao.insert(owners)
        itemDao.insert(item)

        val itemAndOwner = itemDao.findGithubRepositoryAndOwnersByRepositoryId(item.id)
        Assert.assertNotNull(itemAndOwner)
    }

    @Test
    fun `Owner_Id로_쿼리가_되어야한다`() = runBlocking {
        val owners = createTestOwners(1)
        val item = createTestItem(owners.id)
        ownersDao.insert(owners)
        itemDao.insert(item)

        val itemAndOwner = itemDao.findGithubRepositoryAndOwnersByOwnerId(owners.id)
        Assert.assertNotNull(itemAndOwner)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        ioThreadSurrogate.close()
    }
}