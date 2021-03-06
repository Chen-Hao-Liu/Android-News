package edu.gwu.project1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SourcesAdapter(val sources: List<Source>, val mContext: Context, val searchTerm: String) : RecyclerView.Adapter<SourcesAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        // How many rows (total) do you want the adapter to render?
        return sources.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // The RecyclerView needs a "fresh" / new row, so we need to:
        // 1. Read in the XML file for the row type
        // 2. Use the new row to build a ViewHolder to return

        //Step 1
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = layoutInflater.inflate(R.layout.row_source, parent, false)

        //Step 2
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // The RecyclerView is ready to display a new (or recycled) row on the screen
        // for position indicated -- override the UI elements with the correct data
        val currentSource = sources[position]

        /*
        val intent = Intent(this, SourceSelectionActivity::class.java)
        // Sends the input string to the sources page
        intent.putExtra("ITEM", search.getText().toString())
         */

        holder.source.text = currentSource.name
        holder.description.text = currentSource.description

        // Visit url after click on card
        holder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(mContext, ResultsActivity::class.java)
            // Sends the input string to the sources page
            intent.putExtra("newsID", currentSource.id)
            intent.putExtra("newsSource", currentSource.name)
            intent.putExtra("searchTerm", searchTerm)
            mContext.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val source: TextView = itemView.findViewById(R.id.newsHead)
        val description: TextView = itemView.findViewById(R.id.summary)
    }
}