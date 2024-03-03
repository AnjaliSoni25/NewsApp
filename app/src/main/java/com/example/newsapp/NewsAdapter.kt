package com.example.newsapp
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.ItemNewsBinding


class NewsAdapter(private val viewModel: NewsViewModel, private val context: Context) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    var newsList: List<Article> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = newsList[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = newsList.size

    inner class ViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.article = article
            binding.viewModel = viewModel
            binding.context = context
            binding.executePendingBindings()

            Glide.with(context)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.newsImage)

        }

    }
}
