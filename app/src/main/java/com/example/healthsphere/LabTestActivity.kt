package com.example.healthsphere

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class LabTestActivity : AppCompatActivity() {
    private val labTestPackages = arrayOf(
        arrayOf("Package 1 : Full Body Checkup", "299"),
        arrayOf("Package 2 : Blood Glucose Fasting", "199"),
        arrayOf("Package 3 : COVID-19 Antibody IgG", "499"),
        arrayOf("Package 4 : Thyroid Check", "899"),
        arrayOf("Package 5 : Immunity Check", "699")
    )

    private val packageDetails = arrayOf(
        "Blood Glucose Fasting\nComplete Hemogram\nHbA1c\nIron Studies\nKidney Function Test\nLDH Lactate Dehydrogenase, Serum\nLipid Profile\nLiver Function Test",
        "Blood Glucose Fasting",
        "COVID-19 Antibody IgG",
        "Thyroid Profile-Total (T3, T4 & TSH Ultra-sensitive)\nComplete Hemogram\nCRP (C Reactive Protein) Quantitative, Serum\nIron Studies\nKidney Function Test\nVitamin D Total-25 Hydroxy\nLiver Function Test\nLipid Profile",
        "Immunity Check Details"

    )

    private lateinit var backbtn: LinearLayout
    private lateinit var cart_tv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab_test)

        backbtn = findViewById(R.id.backbtn)
        cart_tv = findViewById(R.id.cart_tv)


        val list: MutableList<Map<String, String>> = ArrayList()
        for (i in labTestPackages.indices) {
            val item: MutableMap<String, String> = HashMap()
            item["line1"] = labTestPackages[i][0] // Package name
            item["line2"] = "" // If you have another field, use it here
            item["line3"] = "" // If you have another field, use it here
            item["line4"] = "" // If you have another field, use it here
            item["line5"] = "Cons Fees: ${labTestPackages[i][1]}/-" // Cost
            list.add(item)
        }
        val adapter = SimpleAdapter(
            this,
            list,
            R.layout.multilinestv2, // Layout for each item
            arrayOf("line1", "line5"),
            intArrayOf(R.id.line1, R.id.line5)
        )
        val listView: ListView = findViewById(R.id.listviewdocdetails)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            // Get the clicked item's data
            val selectedItem = list[position]
            val doctorName = selectedItem["line1"] ?: "Unknown"
            val fees = selectedItem["line5"] ?: ""
            // Create an Intent to start BookActivity
            val intent = Intent(this, LabTestDetailsActivity::class.java)
            // Pass data to the new activity
            intent.putExtra("text1", labTestPackages[position][0]) // Package name
            intent.putExtra("text2", packageDetails[position]) // Details
            intent.putExtra("x5", labTestPackages[position][1]) // Cost
            startActivity(intent)
        }
        cart_tv.setOnClickListener{
            val cartIntent = Intent(this, CatLabActivity::class.java)
            startActivity(cartIntent)
        }


        backbtn.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
