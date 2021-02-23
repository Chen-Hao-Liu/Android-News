package edu.gwu.project1

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.anko.doAsync
import java.lang.Exception

class SourceSelectionActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var skipSources: Button
    private lateinit var newsManager: NewsManager
    private lateinit var apiKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source_selection)

        // Set new search item for title
        val intent = getIntent()
        val searchItem: String = intent.getStringExtra("ITEM")!!
        title = "Search for $searchItem"

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

        skipSources = findViewById(R.id.skip)
        recyclerView = findViewById(R.id.recyclerView)
        newsManager = NewsManager()
        apiKey = getString(R.string.news_KEY)

        // Skip source selection (search all sources)
        skipSources.setOnClickListener { v: View ->
            // To be implemented
        }

        // Instantiate adapter for retrieving selected item
        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
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
            // Retrieve list of sources based on current category
            val sources = newsManager.retrieveSources(category, apiKey)

            try{
                runOnUiThread{
                    val adapter = SourcesAdapter(sources)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@SourceSelectionActivity)
                }
            }catch(exception: Exception){
                Log.e("SourceSelectionActivity", "News API failed!", exception)
            }
        }
    }
}