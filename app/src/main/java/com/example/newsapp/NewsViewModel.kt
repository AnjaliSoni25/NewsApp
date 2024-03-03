package com.example.newsapp
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class NewsViewModel : ViewModel() {

    private val newsList = MutableLiveData<List<Article>>()

    fun getNewsList(): MutableLiveData<List<Article>> {
        if (newsList.value == null) {
            fetchNews()
        }
        return newsList
    }

    // Fetch News Data from API using executor service
    private fun fetchNews() {
        val executorService = Executors.newSingleThreadExecutor()

        executorService.execute {
            try {
                val url = URL("https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json")
                val urlConnection = url.openConnection() as HttpURLConnection

                if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    val jsonArray = JSONObject(response.toString()).getJSONArray("articles")

                    val articles = mutableListOf<Article>()

                    for (i in 0 until jsonArray.length()) {
                        val articleJson = jsonArray.getJSONObject(i)
                        val article = Article(
                            articleJson.getJSONObject("source").getString("id"),
                            articleJson.getJSONObject("source").getString("name"),
                            articleJson.getString("author"),
                            articleJson.getString("title"),
                            articleJson.getString("description"),
                            articleJson.getString("url"),
                            articleJson.getString("urlToImage"),
                            formatPublishDate( articleJson.getString("publishedAt")),
                            articleJson.getString("content")
                        )
                        articles.add(article)
                    }

                    // Update UI on the main thread
                    Handler(Looper.getMainLooper()).post {
                        newsList.value = articles
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } finally {
                executorService.shutdown()
            }
        }
    }

    // Open browser window
    fun onNewsItemClick(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }


    //Change Format of Publish Date
    private fun formatPublishDate(publishDate: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(publishDate)

            val outputFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
            return outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    // sort and list articles based on old-to-new and new-to-old
    fun sortArticlesByDate(isChecked: Boolean) {
        val sortedArticles = newsList.value?.let { articles ->
            if (isChecked) {
                articles.sortedByDescending { it.publishedAt }
            } else {
                articles.sortedBy { it.publishedAt }
            }
        }
        newsList.value = sortedArticles
    }
}
