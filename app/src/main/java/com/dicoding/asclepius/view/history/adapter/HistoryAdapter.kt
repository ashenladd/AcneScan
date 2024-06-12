package com.dicoding.asclepius.view.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.entity.ClassificationHistory
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemHistoryBinding
import com.dicoding.asclepius.utils.convertMillisToDateString
import com.dicoding.asclepius.utils.parseClassificationResult
import com.dicoding.asclepius.utils.parseFormatDate

class HistoryAdapter : ListAdapter<ClassificationHistory, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(
            item
        )

    }

    class MyViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClassificationHistory) {
            binding.apply {
                tvHistoryPrediction.text = item.classifications.parseClassificationResult()
                tvDate.text = item.timestamp.toLong().convertMillisToDateString()
                ivHistory.setImageURI(item.imageUri.toUri())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ClassificationHistory>() {
            override fun areItemsTheSame(oldItem: ClassificationHistory, newItem: ClassificationHistory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ClassificationHistory, newItem: ClassificationHistory): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }
        }
    }

}

