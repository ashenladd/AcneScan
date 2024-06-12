package com.dicoding.asclepius.view.history

import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.repository.DataRepository

class HistoryViewModel (
    private val dataRepository: DataRepository
) : ViewModel(){
    fun getHistory() = dataRepository.getHistory()
}