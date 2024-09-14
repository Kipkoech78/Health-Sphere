package com.example.fragments

import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.models.Feeds
import com.example.healthsphere.R
import com.example.recyclerview.FeedAdapterClass
import com.example.sqlite.FeedDatabaseHelper
import org.json.JSONArray
import java.io.InputStream

class FeedFragment : Fragment() {
    private lateinit var adapterClass: FeedAdapterClass
    private lateinit var recyclerView: RecyclerView
    private lateinit var feedsArrayList: ArrayList<Feeds>
    private lateinit var dbHelper: FeedDatabaseHelper

    lateinit var ImageId : Array<String>
    lateinit var heading : Array<String>
    lateinit var headingDesc : Array<String>
    lateinit var workouts : Array<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedsArrayList = arrayListOf()
        recyclerView = view.findViewById(R.id.Feedrecycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        adapterClass = FeedAdapterClass(feedsArrayList)
        recyclerView.adapter = adapterClass
        loadFeedsFromJson()

    }


    private fun loadFeedsFromJson() {
        val json: String?
        try {
            val inputStream: InputStream = requireContext().assets.open("feeds.json")
            json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val feedObject = jsonArray.getJSONObject(i)
                val imageUrl = feedObject.getString("image_url")
                val heading = feedObject.getString("heading")
                val description = feedObject.getString("description")
                feedsArrayList.add(Feeds(imageUrl, heading, description))
            }
            // Notify adapter that data has changed
            adapterClass.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}