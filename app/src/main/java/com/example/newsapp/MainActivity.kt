package com.example.newsapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        viewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        newsAdapter = NewsAdapter(viewModel,this)


        // Observe the LiveData in the ViewModel
        observeData()

        // Set up RecyclerView
        setRecyclerView()
    }



    private fun observeData() {
        viewModel.getNewsList().observe(this) { newsArticles ->
            // Update your UI with the fetched news articles
            setAdapter(newsAdapter, newsArticles)
        }
    }

    // Set up RecyclerView
    private fun setRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = newsAdapter
        }
    }


    // Set up Adapter
    private fun setAdapter(newsAdapter: NewsAdapter, newsArticles: List<Article>) {
        newsAdapter.newsList = newsArticles
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

    }

}
