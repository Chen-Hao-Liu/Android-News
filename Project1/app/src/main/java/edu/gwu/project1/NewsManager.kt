package edu.gwu.project1

import android.content.Context
import android.provider.Settings.Global.getString
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class NewsManager {
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

    fun retrieveSources(category: String, apiKey: String): List<Source> {
        // Either search by category or search by all
        val request =
                Request.Builder()
                    .get()
                    .url("https://newsapi.org/v2/sources?category=$category&apiKey=$apiKey")
                    .build()

        val response: Response = okHttpClient.newCall(request).execute()

        val sourceList = mutableListOf<Source>()
        val responseBody = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrBlank()){
            // Parse JSON body into Source objects
            val json = JSONObject(responseBody)
            val sources = json.getJSONArray("sources")

            for (i in 0 until sources.length()) {
                val curr = sources.getJSONObject(i)
                val id = curr.getString("id")
                val name = curr.getString("name")
                val description = curr.getString("description")
                val url = curr.getString("url")
                val category = curr.getString("category")
                val language = curr.getString("language")
                val country = curr.getString("country")
                sourceList.add(
                    Source(
                        id = id,
                        name = name,
                        description = description,
                        url = url,
                        category = category,
                        language = language,
                        country = country
                    )
                )
            }
        }

        return sourceList
    }
}