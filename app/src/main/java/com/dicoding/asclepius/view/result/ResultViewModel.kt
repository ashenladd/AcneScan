package com.dicoding.asclepius.view.result

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.entity.ClassificationHistory
import com.dicoding.asclepius.data.repository.DataRepository
import kotlinx.coroutines.launch

class ResultViewModel (
    private val repository: DataRepository
) :ViewModel(){
    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved get() = _isSaved

    fun saveResult(history: ClassificationHistory){
        viewModelScope.launch {
            repository.insertHistory(history)
        }
        _isSaved.value = true
    }
}