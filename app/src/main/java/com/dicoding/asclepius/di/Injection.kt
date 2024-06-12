package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.local.database.HistoryRoomDatabase
import com.dicoding.asclepius.data.remote.network.ApiConfig
import com.dicoding.asclepius.data.remote.network.ApiService
import com.dicoding.asclepius.data.repository.DataRepository

object Injection {
    fun provideRepository(context: Context): DataRepository {
        val historyRoomDatabase = HistoryRoomDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return DataRepository.getInstance(apiService, historyRoomDatabase.historyDao())
    }
}