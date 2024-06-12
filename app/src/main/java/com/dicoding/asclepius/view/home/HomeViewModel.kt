package com.dicoding.asclepius.view.home

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.repository.DataRepository
import kotlinx.coroutines.launch
import com.dicoding.asclepius.data.Result

class HomeViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {
    private val _listNews = MutableLiveData<List<ArticlesItem>>()
    val listNews get() = _listNews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading get() = _isLoading

    fun getNews(lifecycleOwner: LifecycleOwner) {
        viewModelScope.launch {
            dataRepository.getNews().observe(lifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        _isLoading.value = false
                        _listNews.value = result.data
                    }
                    is Result.Error -> {
                        _isLoading.value = false
                        Log.d("HomeViewModel", "getNews: ${result.error}")
                    }
                    is Result.Loading -> {
                        _isLoading.value = true
                    }
                }
            }
        }
    }
}