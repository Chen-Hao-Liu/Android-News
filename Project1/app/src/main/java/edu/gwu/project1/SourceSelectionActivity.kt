package edu.gwu.project1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import java.lang.Exception
import java.lang.Thread.sleep

class SourceSelectionActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var skipSources: Button
    private lateinit var newsManager: NewsManager
    private lateinit var apiKey: String
    private lateinit var searchTerm: String
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source_selection)

        // Set new search item for title
        val intent = getIntent()
        val searchItem: String = intent.getStringExtra("ITEM")!!
        title = "Search for $searchItem"
        searchTerm = searchItem

        // Drop down menu
        val category_spinner: Spinner = findViewById(R.id.category_spinner)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            category_spinner.adapter = adapter
        }

        progressBar = findViewById(R.id.progressBarCategory)
        skipSources = findViewById(R.id.skip)
        recyclerView = findViewById(R.id.recyclerView)
        newsManager = NewsManager()
        apiKey = getString(R.string.news_KEY)

        // Skip source selection (search all sources)
        skipSources.setOnClickListener { v: View ->
            // To be implemented
            val intent = Intent(this, ResultsActivity::class.java)
            // Sends the input string to the sources page
            intent.putExtra("newsID", "all")
            intent.putExtra("newsSource", "all")
            intent.putExtra("searchTerm", searchTerm)
            startActivity(intent)
        }

        // Instantiate adapter for retrieving selected item
        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // Retrieve initial sources
                progressBar.visibility = View.VISIBLE
                // An item was selected. You can retrieve the selected item using
                val category = parent.getItemAtPosition(pos).toString()
                // Populate recyclerView cards with category search
                sourceSelection(category)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }
    }

    fun sourceSelection(category: String){
        doAsync{
            sleep(2000)
            // Retrieve list of sources based on current category
            val sources = newsManager.retrieveSources(category, apiKey)

            try{
                runOnUiThread{
                    val adapter = SourcesAdapter(sources, this@SourceSelectionActivity, searchTerm)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@SourceSelectionActivity)
                    // Reset Progressbar
                    progressBar.visibility = View.INVISIBLE
                }
            }catch(exception: Exception){
                Log.e("SourceSelectionActivity", "News API failed!", exception)
                Toast.makeText(
                    this@SourceSelectionActivity,
                    "News API failed! : $exception",
                    Toast.LENGTH_LONG
                ).show()
                // Reset Progressbar
                progressBar.visibility = View.INVISIBLE
            }
        }
    }
}