package com.example.drugs

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.healthsphere.BazeActivity
import com.example.healthsphere.BookActivity
import com.example.healthsphere.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedicineDetailActivity : BazeActivity() {
    private lateinit var backbtn: ImageView
    private lateinit var order: Button
    private lateinit var AddToCart: TextView
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser?.uid // Get the current user ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.medicine_detail_activity)

        backbtn = findViewById(R.id.backbtn)
        order = findViewById(R.id.order)

        backbtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Get data from intent
        val medicineName = intent.getStringExtra("medicine_name")
        val medicineDescription = intent.getStringExtra("medicine_description")
        val medicinePrice = intent.getStringExtra("medicine_price")
        val medicineImage = intent.getStringExtra("medicine_image")

        // Order button logic
        order.setOnClickListener {
            val intent = Intent(this, OrderMedicineActivity::class.java)
            intent.putExtra("price", medicinePrice)
            intent.putExtra("medicine_Name", medicineName)
            startActivity(intent)
        }

        AddToCart = findViewById(R.id.AddToCart)
        AddToCart.setOnClickListener {
            addMedToCart(medicineName, medicineDescription, medicinePrice, medicineImage)
        }

        // Set data to views
        findViewById<TextView>(R.id.medicine_detail_name).text = medicineName
        findViewById<TextView>(R.id.medicine_detail_description).text = medicineDescription
        findViewById<TextView>(R.id.medicine_detail_price).text = "Ksh: $medicinePrice"
        val imageView = findViewById<ImageView>(R.id.medicine_detail_image)
        Glide.with(this).load(medicineImage).into(imageView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addMedToCart(
        medicineName: String?,
        medicineDescription: String?,
        medicinePrice: String?,
        medicineImage: String?
    )
    {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (medicineName == null || medicineDescription == null || medicinePrice == null || medicineImage == null || userId == null) {
            Toast.makeText(this, "Invalid medicine details or user not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val cartItem = hashMapOf(
            "drugName" to medicineName,
            "drugDescription" to medicineDescription,
            "price" to medicinePrice,
            "image" to medicineImage,
            "userId" to userId  // Store userId with the cart item
        )

        db.collection("medicinecart")
            .add(cartItem)
            .addOnSuccessListener {
                hideProgressDialog()
                Toast.makeText(this, "Medicine added to cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                hideProgressDialog()
                e.printStackTrace()
                Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
    }
}