package com.dicoding.asclepius.view.history

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.utils.ViewModelFactory
import com.dicoding.asclepius.view.history.adapter.HistoryAdapter
import com.dicoding.asclepius.view.result.ResultViewModel

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    private val factory by lazy { ViewModelFactory.getInstance(this) }

    private val historyViewModel: HistoryViewModel by viewModels() {
        factory
    }

    private val historyAdapter: HistoryAdapter by lazy {
        HistoryAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupObserver()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvHistory.layoutManager = LinearLayoutManager(
                root.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvHistory.adapter = historyAdapter
        }
    }

    private fun setupObserver() {
        historyViewModel.getHistory().observe(this) {
            historyAdapter.submitList(it)
        }
    }


    private fun setupToolbar() {
        binding.apply {
            lytHistory.toolbar.title = getString(R.string.label_detection_history)
            lytHistory.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
    }
}