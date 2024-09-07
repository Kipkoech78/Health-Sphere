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
        dbHelper = FeedDatabaseHelper(requireContext())
        datainitialize()
        val layoutManager = LinearLayoutManager(context)

        recyclerView = view.findViewById(R.id.Feedrecycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapterClass = FeedAdapterClass(feedsArrayList)
        recyclerView.adapter = adapterClass
    }

    private fun datainitialize() {

        feedsArrayList = arrayListOf()

        // Fetching data from the SQLite database
        val cursor: Cursor = dbHelper.getAllFeeds()

        // Loop through the cursor and add to the Feeds list
        if (cursor.moveToFirst()) {
            do {
                val image = cursor.getString(cursor.getColumnIndexOrThrow(FeedDatabaseHelper.COLUMN_IMAGE_URL))
                val heading = cursor.getString(cursor.getColumnIndexOrThrow(FeedDatabaseHelper.COLUMN_HEADING))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(FeedDatabaseHelper.COLUMN_DESC))

                // Add each feed item to the feedsArrayList
                feedsArrayList.add(Feeds(image, heading, description))
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

}