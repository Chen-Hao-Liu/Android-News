package edu.gwu.project1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class NewsAdapter(val articlesList: MutableList<Article>, val mContext: Context) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    private var articles: MutableList<Article> = articlesList

    fun updateAdapter(newList : List<Article>) {
        for(article in newList){
            articles.add(article)
        }
    }

    override fun getItemCount(): Int {
        // How many rows (total) do you want the adapter to render?
        return articles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // The RecyclerView needs a "fresh" / new row, so we need to:
        // 1. Read in the XML file for the row type
        // 2. Use the new row to build a ViewHolder to return

        //Step 1
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = layoutInflater.inflate(R.layout.row_news, parent, false)

        //Step 2
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // The RecyclerView is ready to display a new (or recycled) row on the screen
        // for position indicated -- override the UI elements with the correct data
        val currentArticle = articles[position]

        // Picasso.get().load(currentArticle.urlToImage).into(holder.newsImage)
        if(!currentArticle.urlToImage.isNullOrBlank()){
            Picasso
                .get()
                .setIndicatorsEnabled(true)

            Picasso
                .get()
                .load(currentArticle.urlToImage)
                .into(holder.newsImage)
        }

        holder.newsHead.text = currentArticle.title
        holder.newsSource.text = currentArticle.name
        holder.summary.text = currentArticle.description

        // Visit url after click on card
        holder.itemView.setOnClickListener { v: View? ->
            val url = Uri.parse(currentArticle.url)
            val intent = Intent(Intent.ACTION_VIEW, url)
            mContext.startActivity(intent)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        val newsHead: TextView = itemView.findViewById(R.id.newsHead)
        val newsSource: TextView = itemView.findViewById(R.id.newsSource)
        val summary: TextView = itemView.findViewById(R.id.summary)
    }
}