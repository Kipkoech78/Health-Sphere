package com.example.drugs

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.healthsphere.R

class MedicineDetailActivity : AppCompatActivity() {
   private lateinit var  backbtn :ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.medicine_detail_activity)
        backbtn = findViewById(R.id.backbtn)
        backbtn.setOnClickListener{
            val backInt = Intent(this, MedicineActivity::class.java)
            backInt.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(backInt)
            finish()
        }
        // Get data from intent
        val medicineName = intent.getStringExtra("medicine_name")
        val medicineDescription = intent.getStringExtra("medicine_description")
        val medicinePrice = intent.getStringExtra("medicine_price")
        val medicineImage = intent.getStringExtra("medicine_image")
        // Set data to views
        findViewById<TextView>(R.id.medicine_detail_name).text = medicineName
        findViewById<TextView>(R.id.medicine_detail_description).text = medicineDescription
        findViewById<TextView>(R.id.medicine_detail_price).text = medicinePrice
        val imageView = findViewById<ImageView>(R.id.medicine_detail_image)
        Glide.with(this).load(medicineImage).into(imageView) //
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}