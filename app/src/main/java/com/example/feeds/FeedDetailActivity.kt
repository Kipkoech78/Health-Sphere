package com.example.feeds

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthsphere.R

class FeedDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_feed_detail)

        val feedHeading = intent.getStringExtra("heading")
        val feedDescription = intent.getStringExtra("description")
        val headingTextView = findViewById<TextView>(R.id.feed_heading)
        val descriptionTextView = findViewById<TextView>(R.id.feed_description)

        headingTextView.text = feedHeading
        descriptionTextView.text = feedDescription
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}