package com.dicoding.asclepius.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.local.dao.HistoryDao
import com.dicoding.asclepius.data.local.entity.ClassificationHistory
import com.dicoding.asclepius.data.remote.network.ApiService
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.Result

class DataRepository private constructor(
    private val apiService: ApiService,
    private val historyDao: HistoryDao
) {
    suspend fun getNews(): LiveData<Result<List<ArticlesItem>>> =
        liveData{
            emit(Result.Loading)
            try {
                val response = apiService.getNews("id", "health", BuildConfig.API_KEY)
                val item :List<ArticlesItem> = response.articles?.map {
                    ArticlesItem(
                        it?.publishedAt,
                        it?.author,
                        it?.urlToImage,
                        it?.description,
                        it?.source,
                        it?.title,
                        it?.url,
                        it?.content
                    )
                } ?: emptyList()

                emit(Result.Success(item))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }

    }
    fun getHistory() = historyDao.getAllHistory()

    suspend fun insertHistory(classificationHistory: ClassificationHistory) = historyDao.insertHistory(classificationHistory)

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(
            apiService: ApiService,
            historyDao: HistoryDao
        ): DataRepository =
            instance ?: synchronized(this) {
                instance ?: DataRepository(
                    apiService,
                    historyDao
                ).also { instance = it }
            }
    }

}