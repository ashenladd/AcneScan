package com.dicoding.asclepius.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.repository.DataRepository
import com.dicoding.asclepius.di.Injection
import com.dicoding.asclepius.view.history.HistoryViewModel
import com.dicoding.asclepius.view.home.HomeViewModel
import com.dicoding.asclepius.view.result.ResultViewModel

class ViewModelFactory private constructor(
    private val dataRepository: DataRepository,
) : ViewModelProvider.NewInstanceFactory(
) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                dataRepository,
            ) as T
        }else if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(
                dataRepository,
            ) as T
        }else if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(
                dataRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(context),
                )
            }.also { instance = it }
    }
}