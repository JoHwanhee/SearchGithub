package com.hwanhee.search_github.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.hwanhee.search_github.model.entity.GithubRepositoryItemEntity
import com.hwanhee.search_github.model.entity.GithubRepositoryOwnerEntity
import com.hwanhee.search_github.model.entity.GithubTopicEntity
import com.hwanhee.search_github.model.entity.SearchWordEntity
import java.time.LocalDateTime


@Database(
    entities = [
        SearchWordEntity::class,
        GithubRepositoryOwnerEntity::class,
        GithubRepositoryItemEntity::class,
        GithubTopicEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchWordDao(): SearchWordDao
    abstract fun githubRepositoryOwnerDao(): GithubRepositoryOwnerDao
    abstract fun githubRepositoryItemDao(): GithubRepositoryItemDao
    abstract fun githubTopicDao(): GithubTopicDao
}

object Converters {
    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            LocalDateTime.parse(dateString)
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}