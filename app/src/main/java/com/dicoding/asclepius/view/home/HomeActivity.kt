package com.dicoding.asclepius.view.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.databinding.ActivityHomeBinding
import com.dicoding.asclepius.utils.ViewModelFactory
import com.dicoding.asclepius.view.MainActivity
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.home.adapter.HomeAdapter

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val factory by lazy { ViewModelFactory.getInstance(this) }

    private val homeViewModel: HomeViewModel by viewModels() {
        factory
    }
    private val homeAdapter: HomeAdapter by lazy {
        HomeAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObserver()
        setupData()
        setupRecyclerView()
        setupClick()
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvNews.layoutManager = LinearLayoutManager(
                this@HomeActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            homeAdapter.setOnItemClickListener(object : HomeAdapter.OnItemClickListener {
                override fun onItemClick(url: String) {
                    navigateToUrl(url)
                }
            })
            rvNews.adapter = homeAdapter

        }
    }

    private fun navigateToUrl(url: String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(
            "query",
            url
        )
        startActivity(intent)
    }

    private fun setupData() {
        homeViewModel.getNews(this)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupObserver() {
        homeViewModel.listNews.observe(this) {
            homeAdapter.submitList(it)
        }
        homeViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupClick(){
        binding.apply {
            btnHistory.setOnClickListener {
                navigateToHistory()
            }
            btnDetectNow.setOnClickListener {
                navigateToMain()
            }
        }

    }

    private fun navigateToMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHistory(){
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

}