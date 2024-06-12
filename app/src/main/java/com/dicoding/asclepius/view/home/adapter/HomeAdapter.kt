package com.dicoding.asclepius.view.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemNewsBinding
import com.dicoding.asclepius.utils.parseFormatDate

class HomeAdapter : ListAdapter<ArticlesItem, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(
            item,
            onItemClickListener
        )

    }

    class MyViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArticlesItem, onItemClickListener: OnItemClickListener) {
            binding.apply {
                tvAuthor.text = item.author.orEmpty()
                tvNewsName.text = item.title.orEmpty()
                tvDate.text = item.publishedAt?.parseFormatDate().orEmpty()
                Glide.with(itemView.context)
                    .load(item.urlToImage)
                    .placeholder(R.drawable.img_news)
                    .into(ivNews)
                root.setOnClickListener {
                    onItemClickListener.onItemClick(item.url.orEmpty())
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(url: String)
    }

}