package com.example.drugs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Adapters.MedicineAdapter
import com.example.healthsphere.R
import com.example.models.Medicine
import com.example.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore


class MedicineActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val medicines = mutableListOf<Medicine>()
    private lateinit var medicineAdapter: MedicineAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.medicine_activity)

        recyclerView = findViewById(R.id.Medicinerecycler_view)
        val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, Context.MODE_PRIVATE)
        val userADMIN = sharedPreferences.getString(Constants.ADMIN, "")

        recyclerView.layoutManager = LinearLayoutManager(this)
        medicineAdapter = MedicineAdapter(medicines){ medicine ->
            val intent = Intent(this, MedicineDetailActivity::class.java).apply{
                putExtra("medicine_name", medicine.drugName)
                putExtra("medicine_description", medicine.description)
                putExtra("medicine_price", medicine.price)
                putExtra("medicine_image", medicine.drugImg)
            }
            startActivity(intent)

        }
       recyclerView.adapter = medicineAdapter

        fetchMedicine()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun fetchMedicine(){
        db.collection(Constants.DRUG)
            .get()
            .addOnSuccessListener{result ->
                for(document in result){
                    val medicine = document.toObject(Medicine::class.java)
                    medicines.add(medicine)

                }
                medicineAdapter.notifyDataSetChanged() // Notify the adapter to update the RecyclerView


            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }

    }
}