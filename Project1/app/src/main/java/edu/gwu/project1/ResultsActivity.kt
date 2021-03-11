package edu.gwu.project1

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import java.lang.Exception
import java.lang.Thread.sleep

class ResultsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articlesManager: ArticlesManager
    private lateinit var apiKey: String
    private lateinit var adapter: NewsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var savedState: Parcelable
    private lateinit var newsID: String
    private lateinit var searchTerm: String
    private lateinit var newsSource: String
    private var page: Int = 1
    // load is a flag that prevents users from loading a new page while currently loading a new page
    // meaning if they scroll to refresh, and the progressBar is still going on, scrolling again won't
    // do anything.
    private var load: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Retrieve the resources needed for API call
        val intent = getIntent()
        newsID = intent.getStringExtra("newsID")!!
        searchTerm = intent.getStringExtra("searchTerm")!!
        newsSource = intent.getStringExtra("newsSource")!!

        // Determine either skip sources or use sources
        if(newsID == "all") {
            title = "Search for '$searchTerm'"
        }else{
            title = "$newsSource results for '$searchTerm'"
        }

        progressBar = findViewById(R.id.resultProgress)
        recyclerView = findViewById(R.id.resultsRecyclerView)
        articlesManager = ArticlesManager()
        apiKey = getString(R.string.news_KEY)

        // Retrieve first page
        progressBar.visibility = View.VISIBLE
        articleSelection()

        // infinite scroll implementation
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                val total = recyclerView.layoutManager!!.itemCount

                // If user is attempting to scroll past final view
                if (total == lastPosition && load) {
                    load = false
                    Log.d("ResultsActivity", "Position: $lastPosition, Total: $total, Page: $page")
                    // Save position
                    savedState = recyclerView.layoutManager?.onSaveInstanceState()!!
                    progressBar.visibility = View.VISIBLE
                    articleSelection()
                }
            }
        })
    }

    fun articleSelection(){
        doAsync{
            sleep(2000)
            // Retrieve list of news articles based on current category
            var articleURL = "https://newsapi.org/v2/everything?sources=$newsID&q=$searchTerm&page=$page&apiKey=$apiKey"
            if(newsID == "all"){
                articleURL = "https://newsapi.org/v2/everything?q=$searchTerm&page=$page&apiKey=$apiKey"
            }

            val articles = articlesManager.retrieveArticles(articleURL)

            try{
                if(articles.isNotEmpty()) {
                    runOnUiThread {
                        // Initial page
                        if (page == 1) {
                            adapter = NewsAdapter(articles.toMutableList(), this@ResultsActivity)
                            recyclerView.adapter = adapter
                            layoutManager = LinearLayoutManager(this@ResultsActivity)
                            recyclerView.layoutManager = layoutManager
                            // Further paging
                        } else {
                            adapter.updateAdapter(articles)
                            adapter.notifyDataSetChanged()
                            // Restore position
                            recyclerView.layoutManager?.onRestoreInstanceState(savedState)
                        }
                        // End progress bar
                        progressBar.visibility = View.INVISIBLE
                        // allow load
                        load = true
                        // Increment page to fetch next set of results
                        page++
                    }
                }else{
                    runOnUiThread {
                        var notification = "Search on $newsSource yielded no further results for '$searchTerm'"
                        if(newsID == "all"){
                            notification = "Search yielded no further results for '$searchTerm'"
                        }
                        Log.d("ResultsActivity", notification)
                        val toast = Toast.makeText(
                            this@ResultsActivity,
                            notification,
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                        // End progress bar
                        progressBar.visibility = View.INVISIBLE
                        // allow load
                        load = true
                    }
                }
            }catch(exception: Exception){
                Log.e("ResultsActivity", "News API failed!", exception)
                Toast.makeText(
                    this@ResultsActivity,
                    "News API failed! : $exception",
                    Toast.LENGTH_LONG
                ).show()

                // End progress bar
                progressBar.visibility = View.INVISIBLE
                // allow load
                load = true
            }
        }
    }
}