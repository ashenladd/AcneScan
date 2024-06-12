package com.dicoding.asclepius.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.ClassificationHistory

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(history: ClassificationHistory)

    @Query("SELECT * FROM ClassificationHistory")
    fun getAllHistory(): LiveData<List<ClassificationHistory>>
}