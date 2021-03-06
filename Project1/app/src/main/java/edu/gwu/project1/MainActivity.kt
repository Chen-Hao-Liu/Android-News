package edu.gwu.project1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private lateinit var search: EditText
    private lateinit var searchButton: Button
    private lateinit var viewMap: Button
    private lateinit var viewHead: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search = findViewById(R.id.search)
        searchButton = findViewById(R.id.searchButton)
        viewMap = findViewById(R.id.viewMap)
        viewHead = findViewById(R.id.viewHead)

        // sharedPreferences for category
        val preferences = getSharedPreferences("home", Context.MODE_PRIVATE)

        // If previous saved search exists, use it
        val savedCategory = preferences.getString("searchTerm", "")!!
        if(savedCategory != "") {
            search.setText(savedCategory)
            searchButton.isEnabled = true
        }else{
            // Underneath, Kotlin will convert this to setEnabled(false)
            searchButton.isEnabled = false
        }

        searchButton.setOnClickListener { v: View ->
            val intent = Intent(this, SourceSelectionActivity::class.java)
            // Save category in preferences
            val prefEditor = preferences.edit()
            prefEditor.putString("searchTerm", search.getText().toString())
            prefEditor.commit()

            // Sends the input string to the sources page
            intent.putExtra("ITEM", search.getText().toString())
            startActivity(intent)
        }

        viewMap.setOnClickListener { v: View ->
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        viewHead.setOnClickListener { v: View ->
            val intent = Intent(this, TopHeadlinesActivity::class.java)
            startActivity(intent)
        }

        search.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Enable button after input detected in search bar
            val searchInput: String = search.getText().toString()
            val enableButton: Boolean = searchInput.isNotEmpty()
            searchButton.isEnabled = enableButton
        }
    }
}