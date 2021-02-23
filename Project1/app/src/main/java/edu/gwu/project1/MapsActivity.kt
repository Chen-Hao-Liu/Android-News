package edu.gwu.project1

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import java.lang.Double.doubleToRawLongBits
import java.lang.Double.longBitsToDouble

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var recyclerView: RecyclerView
    private lateinit var resultsFor: TextView
    private lateinit var cardView: CardView
    private lateinit var closeBtn: ImageButton
    private lateinit var articlesManager: ArticlesManager
    private lateinit var apiKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        recyclerView = findViewById(R.id.horiRecycler)
        resultsFor = findViewById(R.id.newLocation)
        cardView = findViewById(R.id.newsCard)
        closeBtn = findViewById(R.id.closeNews)
        apiKey = getString(R.string.news_KEY)

        title = "News by Location"

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        //Triggers the loading of the map
        mapFragment.getMapAsync(this)

        closeBtn.setOnClickListener { v: View ->
            // To be implemented
            cardView.visibility = View.INVISIBLE
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        articlesManager = ArticlesManager()

        val preferences = getSharedPreferences("android-news", Context.MODE_PRIVATE)

        // load previous preference if it exists
        if(preferences.contains("country")){
            mMap.clear()
            // load saved preferences
            val pA = preferences.getString("postalAddress", "")!!
            val st = preferences.getString("state", "")!!
            val co = preferences.getString("country", "")!!
            val se = preferences.getString("searchTerm", "")!!
            // convert long back to double
            val clat = longBitsToDouble(preferences.getLong("lat", 0))
            val clon = longBitsToDouble(preferences.getLong("lon", 0))
            // make changes
            doRecyclerView(pA, st, co, se, clat, clon)
        }

        // When user moves marker
        mMap.setOnMapLongClickListener { coords: LatLng ->
            mMap.clear()

            // Perform geocoding on separate thread
            doAsync {
                val geocoder: Geocoder = Geocoder(this@MapsActivity)
                // Retrieve list of addresses from geocoding
                val results: List<Address> = try{
                    geocoder.getFromLocation(
                        coords.latitude,
                        coords.longitude,
                        10
                    )
                } catch (e: Exception){
                    Log.e("MapsActivity", "Geocoder failed", e)
                    listOf<Address>()
                }

                // map related changes should be done on UI thread
                runOnUiThread{
                    if (results.isNotEmpty()) {
                        val firstResult = results.first()
                        val postalAddress = firstResult.getAddressLine(0)
                        val country = firstResult.getCountryName()
                        val state = firstResult.getAdminArea()
                        // If location not within US, use country as search term
                        val searchTerm = if (country == "United States") state else country

                        // Save current data in preferences
                        val prefEditor = preferences.edit()
                        prefEditor.putString("postalAddress", postalAddress)
                        prefEditor.putString("country", country)
                        prefEditor.putString("state", state)
                        prefEditor.putString("searchTerm", searchTerm)
                        // Convert to long to preserve double coordinates
                        prefEditor.putLong("lat", doubleToRawLongBits(coords.latitude))
                        prefEditor.putLong("lon", doubleToRawLongBits(coords.longitude))
                        prefEditor.commit()

                        // make changes
                        doRecyclerView(postalAddress, state, country, searchTerm, coords.latitude, coords.longitude)
                    } else {
                        Log.d("MapsActivity", "No results from geocoder!")
                        val toast = Toast.makeText(
                            this@MapsActivity,
                            "No results for location!",
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                    }
                }
            }
        }
    }

    // Populate recyclerView with cards, updates marker position, creates toast notification
    fun doRecyclerView(postalAddress: String, state: String, country: String, searchTerm: String, lat: Double, lon: Double){
        // Notification for where user clicked
        val toast = Toast.makeText(
            this@MapsActivity,
            "You clicked: $postalAddress!",
            Toast.LENGTH_LONG
        )
        toast.show()

        val coords = LatLng(lat, lon)

        // Show results for state if within US, otherwise show results for country
        if(country == "United States") {
            mMap.addMarker(
                MarkerOptions().position(coords)
                    .title("Showing results for $state, $country")
            )
        }else{
            mMap.addMarker(
                MarkerOptions().position(coords)
                    .title("Showing results for $country")
            )
        }

        // Retrieve list of articles from API but do so on separate thread
        doAsync{
            try{
                val articles = articlesManager.retrieveArticles(searchTerm, apiKey)

                // Display card contents through recyclerview
                runOnUiThread{
                    cardView.visibility = View.VISIBLE
                    resultsFor.setText("Results for $searchTerm");
                    val adapter = NewsAdapter(articles, this@MapsActivity)
                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(this@MapsActivity, LinearLayoutManager.HORIZONTAL, false)
                }
            }catch(exception: java.lang.Exception){
                Log.e("MapsActivity", "News API failed!", exception)
            }
        }

        // Smoothly animated zoom onto coords
        val zoomLevel = 8.0f
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coords, zoomLevel)
        )
    }
}