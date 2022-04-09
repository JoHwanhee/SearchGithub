package com.hwanhee.search_github.db

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg elements: T)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(elements: List<T>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg elements: T)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(elements: List<T>)

    @Transaction
    @Update
    suspend fun update(vararg elements: T)

    @Transaction
    @Update
    suspend fun update(elements: List<T>)

    @Transaction
    @Delete
    suspend fun delete(vararg elements: T)

    @Transaction
    @Delete
    suspend fun delete(elements: List<T>)
}