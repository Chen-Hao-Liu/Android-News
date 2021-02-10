package edu.gwu.project1

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SourceSelectionActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_source_selection)

        val intent = getIntent()
        val searchItem: String = intent.getStringExtra("ITEM")!!

        title = "Search for $searchItem"

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

        recyclerView = findViewById(R.id.recyclerView)
        val sources = getFakeSources()
        val adapter = SourcesAdapter(sources)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun getFakeSources(): List<Source> {
        return listOf(
            Source(
                id = "abc-news",
                name = "ABC News",
                description = "Your trusted source for breaking news, analysis, exclusive interviews, headlines, and videos at ABCNews.com.",
                url = "https://abcnews.go.com",
                category = "general",
                language = "en",
                country = "us"
            ),
            Source(
                id = "abc-news-au",
                name = "ABC News (AU)",
                description = "Australia's most trusted source of local, national and world news. Comprehensive, independent, in-depth analysis, the latest business, sport, weather and more.",
                url = "http://www.abc.net.au/news",
                category = "general",
                language = "en",
                country = "au"
            ),
            Source(
                id = "aftenposten",
                name = "Aftenposten",
                description = "Norges ledende nettavis med alltid oppdaterte nyheter innenfor innenriks, utenriks, sport og kultur.",
                url = "https://www.aftenposten.no",
                category = "general",
                language = "no",
                country = "no"
            ),
            Source(
                id = "al-jazeera-english",
                name = "Al Jazeera English",
                description = "News, analysis from the Middle East and worldwide, multimedia and interactives, opinions, documentaries, podcasts, long reads and broadcast schedule.",
                url = "http://www.aljazeera.com",
                category = "general",
                language = "en",
                country = "us"
            ),
            Source(
                id = "ansa",
                name = "ANSA.it",
                description = "Agenzia ANSA: ultime notizie, foto, video e approfondimenti su: cronaca, politica, economia, regioni, mondo, sport, calcio, cultura e tecnologia.",
                url = "http://www.ansa.it",
                category = "general",
                language = "it",
                country = "it"
            ),
            Source(
                id = "argaam",
                name = "Argaam",
                description = "ارقام موقع متخصص في متابعة سوق الأسهم السعودي تداول - تاسي - مع تغطيه معمقة لشركات واسعار ومنتجات البتروكيماويات , تقارير مالية الاكتتابات الجديده ",
                url = "http://www.argaam.com",
                category = "business",
                language = "ar",
                country = "sa"
            ),
            Source(
                id = "ars-technica",
                name = "Ars Technica",
                description = "The PC enthusiast's resource. Power users and the tools they love, without computing religion.",
                url = "http://arstechnica.com",
                category = "technology",
                language = "en",
                country = "us"
            ),
            Source(
                id = "ary-news",
                name = "Ary News",
                description = "ARY News is a Pakistani news channel committed to bring you up-to-the minute Pakistan news and featured stories from around Pakistan and all over the world.",
                url = "https://arynews.tv/ud/",
                category = "general",
                language = "ud",
                country = "pk"
            ),
            Source(
                id = "associated-press",
                name = "Associated Press",
                description = "The AP delivers in-depth coverage on the international, politics, lifestyle, business, and entertainment news.",
                url = "https://apnews.com/",
                category = "general",
                language = "en",
                country = "us"
            ),
            Source(
                id = "australian-financial-review",
                name = "Australian Financial Review",
                description = "The Australian Financial Review reports the latest news from business, finance, investment and politics, updated in real time. It has a reputation for independent, award-winning journalism and is essential reading for the business and investor community.",
                url = "http://www.afr.com",
                category = "business",
                language = "en",
                country = "au"
            ),
            Source(
                id ="axios",
                name = "Axios",
                description = "Axios are a new media company delivering vital, trustworthy news and analysis in the most efficient, illuminating and shareable ways possible.",
                url = "https://www.axios.com",
                category = "general",
                language = "en",
                country = "us"
            ),
            Source(
                id = "bbc-news",
                name = "BBC News",
                description = "Use BBC News for up-to-the-minute news, breaking news, video, audio and feature stories. BBC News provides trusted World and UK news as well as local and regional perspectives. Also entertainment, business, science, technology and health news.",
                url = "http://www.bbc.co.uk/news",
                category = "general",
                language = "en",
                country = "gb"
            ),
            Source(
                id = "bbc-sport",
                name = "BBC Sport",
                description = "The home of BBC Sport online. Includes live sports coverage, breaking news, results, video, audio and analysis on Football, F1, Cricket, Rugby Union, Rugby League, Golf, Tennis and all the main world sports, plus major events such as the Olympic Games.",
                url = "http://www.bbc.co.uk/sport",
                category = "sports",
                language = "en",
                country = "gb"
            ),
            Source(
                id = "bild",
                name = "Bild",
                description = "Die Seite 1 für aktuelle Nachrichten und Themen, Bilder und Videos aus den Bereichen News, Wirtschaft, Politik, Show, Sport, und Promis.",
                url = "http://www.bild.de",
                category = "general",
                language = "de",
                country = "de"
            )
        )
    }
}