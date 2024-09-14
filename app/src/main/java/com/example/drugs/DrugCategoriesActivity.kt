package com.example.drugs

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthsphere.BazeActivity
import com.example.healthsphere.MainActivity
import com.example.healthsphere.R
import com.example.models.Medicine
import com.example.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class DrugCategoriesActivity : BazeActivity() {
    private lateinit var backbtn: ImageView
    private lateinit var cardMeasles: CardView
    private lateinit var cardheartProblems : CardView
    private lateinit var cardCommonCold: CardView
    private lateinit var cardMalaria: CardView
    private lateinit var cardOverTheCounter: CardView
    private val medicines = mutableListOf<Medicine>()

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.drug_categories_activity)
        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        cardMeasles = findViewById(R.id.cardMeasles)
        cardOverTheCounter = findViewById(R.id.cardOverTheCounter)
        cardMalaria = findViewById(R.id.cardMalaria)
        cardCommonCold = findViewById(R.id.cardCommonCold)
        cardheartProblems = findViewById(R.id.cardheartProblems)

        backbtn = findViewById(R.id.backbtn)
        backbtn.setOnClickListener{
            val backInt = Intent(this, MainActivity::class.java)
            startActivity(backInt)
        }
        cardheartProblems.setOnClickListener{
            fetchMedicine("Heart Problems")
        }
        cardCommonCold.setOnClickListener{
            fetchMedicine("Common Cold")
        }
        cardMalaria.setOnClickListener{
            fetchMedicine("Over The counter")
        }
        cardOverTheCounter.setOnClickListener {
            fetchMedicine("Over The counter")
        }
        cardMeasles.setOnClickListener {
            fetchMedicine("measles")
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun fetchMedicine(s: String) {
        showProgressDialog(resources.getString(R.string.loading))
        db.collection(Constants.DRUG)
            .whereEqualTo("category", s)
            .get()
            .addOnSuccessListener{result ->
                hideProgressDialog()
                val medicineDet = result.map { document ->
                    val medicine = document.toObject(Medicine::class.java)
                    arrayOf(
                        medicine.drugImg,
                        medicine.drugName,
                        medicine.price,
                        medicine.description
                    )
                }.toTypedArray()
                val intent = Intent(this, MedicineActivity::class.java)
                intent.putExtra("title", s)
                intent.putExtra("medicine", medicineDet)
                startActivity(intent)
            }
            .addOnFailureListener { e->
                e.printStackTrace()
                hideProgressDialog()
            }
    }
}