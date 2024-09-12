package com.example.healthsphere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.drugs.AddDrugsActivity

import com.example.models.Doctor
import com.example.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class FindDoctors : BazeActivity() {
    private lateinit var backbtn: LinearLayout
    private lateinit var cardFindFamDoc: CardView
    private lateinit var cardDentist: CardView
    private lateinit var cardSergion: CardView
    private lateinit var adminBtn: CardView
    private lateinit var optician: CardView
    private lateinit var Cardphamacy: CardView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_doctors)
        backbtn = findViewById(R.id.backbtn)
        cardFindFamDoc = findViewById(R.id.cardFindFamDoc)
        Cardphamacy = findViewById(R.id.Cardphamacy)
        optician = findViewById(R.id.optician)
        adminBtn = findViewById(R.id.adminBtn)
        cardSergion = findViewById(R.id.cardSergion)
        cardDentist = findViewById(R.id.cardDentist)



        val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, Context.MODE_PRIVATE)
        val userADMIN = sharedPreferences.getString(Constants.ADMIN, "")
        // Show the button only if the user is an admin
        if (userADMIN == "admin") {
            adminBtn.visibility = View.VISIBLE
        } else {
            adminBtn.visibility = View.GONE
        }
        Log.d("UserRole", "Role: $userADMIN")
        adminBtn.setOnClickListener{
            if (userADMIN == "admin"){
                val intent = Intent(this, AddDrugsActivity::class.java)
                intent.putExtra("userEmail", userADMIN) // Pass the email to AddDrugsActivity
                startActivity(intent)
            } else {
                    // Show a message that access is denied
                    Toast.makeText(this, "Access Denied! You are not authorized to add drugs.", Toast.LENGTH_SHORT).show()
                }
        }

        backbtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        Cardphamacy.setOnClickListener {
            fetchDoctors("Pharmacy")
        }

        cardSergion.setOnClickListener {
            fetchDoctors("Surgeon")
        }

        optician.setOnClickListener {
            fetchDoctors("Optician")
        }

        cardDentist.setOnClickListener {
            fetchDoctors("Dentist")
        }

        cardFindFamDoc.setOnClickListener {
            fetchDoctors("Family Physician")
        }
    }

    private fun fetchDoctors(category: String) {
        showProgressDialog(resources.getString(R.string.please_wait))
        db.collection("doctors")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { result ->
                hideProgressDialog()
                val doctorDetails = result.map { document ->
                    val doctor = document.toObject(Doctor::class.java)
                    arrayOf(
                        doctor.name,
                        doctor.address,
                        doctor.experience,
                        doctor.email,
                        doctor.fees
//                        "Doctor Name: ${doctor.name}",
//                        "Hospital Address: ${doctor.address}",
//                        "Exp: ${doctor.experience}",
//                        " ${doctor.email}",
//                        "Charges: ${doctor.fees}"
                    )
                }.toTypedArray()

                val intent = Intent(this, DoctorDetails::class.java)
                intent.putExtra("title", category)
                intent.putExtra("doctorDetails", doctorDetails)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                // Handle the error
                Toast.makeText(this, "error fetching Doctor data ${exception}", Toast.LENGTH_SHORT)
                    .show()
                hideProgressDialog()
            }
    }
}
