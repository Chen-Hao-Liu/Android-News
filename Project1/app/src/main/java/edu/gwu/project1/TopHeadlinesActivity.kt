package edu.gwu.project1

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import java.lang.Exception
import java.lang.Thread.sleep

class TopHeadlinesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var articlesManager: ArticlesManager
    private lateinit var apiKey: String
    private lateinit var category: String
    private lateinit var adapter: NewsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var categoryAdapter: ArrayAdapter<CharSequence>
    private lateinit var progressBar: ProgressBar
    private lateinit var savedState: Parcelable
    private var page: Int = 1
    // load is a flag that prevents users from loading a new page while currently loading a new page
    // meaning if they scroll to refresh, and the progressBar is still going on, scrolling again won't
    // do anything.
    private var load: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_headlines)

        progressBar = findViewById(R.id.resultProgress)

        // sharedPreferences for category
        val preferences = getSharedPreferences("headlines", Context.MODE_PRIVATE)

        title = "Top Headlines"

        // Drop down menu
        val category_spinner: Spinner = findViewById(R.id.head_spinner)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            categoryAdapter = adapter
            category_spinner.adapter = categoryAdapter
        }

        // If previous saved category exists, use it
        val savedCategory = preferences.getString("category", "")!!
        if(savedCategory != "") {
            category_spinner.setSelection(categoryAdapter.getPosition(savedCategory))
        }

        recyclerView = findViewById(R.id.resultsRecyclerView)
        articlesManager = ArticlesManager()
        apiKey = getString(R.string.news_KEY)

        // Instantiate adapter for retrieving selected item
        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                category = parent.getItemAtPosition(pos).toString()
                // Save category in preferences
                val prefEditor = preferences.edit()
                prefEditor.putString("category", category)
                prefEditor.commit()
                // start at page 1
                page = 1
                progressBar.visibility = View.VISIBLE
                // Populate recyclerView cards with category search
                articleSelection(category)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }

        // infinite scroll implementation
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastPosition = layoutManager.findLastCompletelyVisibleItemPosition() + 1
                val total = recyclerView.layoutManager!!.itemCount

                // If user is attempting to scroll past final view
                if (total == lastPosition && load) {
                    load = false
                    Log.d("TopHeadlinesActivity", "Position: $lastPosition, Total: $total, Page: $page")
                    // Save position
                    savedState = recyclerView.layoutManager?.onSaveInstanceState()!!
                    progressBar.visibility = View.VISIBLE
                    articleSelection(category)
                }
            }
        })
    }

    fun articleSelection(category: String){
        doAsync{
            sleep(2000)
            // Retrieve list of news articles based on current category
            val articleURL: String = "https://newsapi.org/v2/top-headlines?page=$page&category=$category&apiKey=$apiKey"
            val articles = articlesManager.retrieveArticles(articleURL)

            try{
                if(articles.isNotEmpty()) {
                    runOnUiThread {
                        if (page == 1) {
                            adapter =
                                NewsAdapter(articles.toMutableList(), this@TopHeadlinesActivity)
                            recyclerView.adapter = adapter
                            layoutManager = LinearLayoutManager(this@TopHeadlinesActivity)
                            recyclerView.layoutManager = layoutManager
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
                        // increment for paging functionality
                        page++
                    }
                }else{
                    runOnUiThread {
                        var notification = "Search yielded no further results for $category"
                        Log.d("TopHeadlinesActivity", notification)
                        val toast = Toast.makeText(
                            this@TopHeadlinesActivity,
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
                Log.e("TopHeadlinesActivity", "News API failed!", exception)
                Toast.makeText(
                    this@TopHeadlinesActivity,
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