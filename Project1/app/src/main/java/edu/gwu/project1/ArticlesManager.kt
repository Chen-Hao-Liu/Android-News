package edu.gwu.project1

import android.content.res.Resources
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class ArticlesManager {
    // The OkHttpClient will facilitate the complexities of networking
    // with Twitter's servers
    val okHttpClient: OkHttpClient

    // An init block allows us to do extra logic during class initialization
    init {
        val builder = OkHttpClient.Builder()

        // Set our networking client up to log all requests & responses
        // to console
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)

        okHttpClient = builder.build()
    }

    fun retrieveArticles(searchTerm: String, apiKey: String): List<Article> {
        val request =
            Request.Builder()
                .get()
                .url("https://newsapi.org/v2/everything?qInTitle=$searchTerm&apiKey=$apiKey")
                .build()

        val response: Response = okHttpClient.newCall(request).execute()

        val articleList = mutableListOf<Article>()
        val responseBody = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrBlank()){
            // Parse JSON body into Source objects
            val json = JSONObject(responseBody)
            val articles = json.getJSONArray("articles")

            for (i in 0 until articles.length()) {
                val curr = articles.getJSONObject(i)
                val source = curr.getJSONObject("source")
                val id = source.getString("id")
                val name = source.getString("name")
                val author = curr.getString("author")
                val title = curr.getString("title")
                val description = curr.getString("description")
                val url = curr.getString("url")
                val urlToImage = curr.getString("urlToImage")
                val publishedAt = curr.getString("publishedAt")
                val content = curr.getString("content")
                articleList.add(
                    Article(
                        id = id,
                        name = name,
                        author = author,
                        title = title,
                        description = description,
                        url = url,
                        urlToImage = urlToImage,
                        publishedAt = publishedAt,
                        content = content
                    )
                )
            }
        }

        return articleList
    }
}