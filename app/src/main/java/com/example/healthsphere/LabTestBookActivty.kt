package com.example.healthsphere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firestore.FirestoreClass
import com.example.firestore.Firestorecart
import com.example.models.CartItem
import com.example.models.Order
import com.example.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LabTestBookActivty : BazeActivity() {
    private lateinit var uploadName:EditText
    private lateinit var et_address: EditText
    private lateinit var et_email : EditText
    private lateinit var et_fees: TextView
    private lateinit var bookingButton: Button
    private lateinit var backbtn: LinearLayout
   // private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab_test_book_activty)
        uploadName = findViewById(R.id.uploadName)
        et_address = findViewById(R.id.et_address)
        et_email = findViewById(R.id.et_email)
        et_fees = findViewById(R.id.et_fees)
        bookingButton = findViewById(R.id.bookingButton)
        backbtn = findViewById(R.id.backbtn)

        auth = FirebaseAuth.getInstance()

        val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        val userEmail = sharedPreferences.getString(Constants.LOGGED_IN_USEREMAIL, "")!!

        et_fees.isFocusable = false
        et_fees.isFocusableInTouchMode = false

        uploadName.isFocusable = false
        uploadName.isFocusableInTouchMode = false

        et_email.isFocusable = false
        et_email.isFocusableInTouchMode = false


        val intent = intent
        val date = intent.getStringExtra("date") ?: ""
        val time = intent.getStringExtra("time") ?: ""
        val price = intent.getStringExtra("price") ?: ""
        val pricestr = intent.getStringExtra("price")?.replace("Ksh ", "") ?: "0.0"
        val product = intent.getStringExtra("packageName") ?: ""

        et_fees.text = price
        uploadName.setText(username)
        et_email.setText(userEmail)

        //val price = pricestr.toFloatOrNull() ?: 0.0f
        bookingButton.setOnClickListener{
            showProgressDialog(resources.getString(R.string.please_wait))
            val userId = auth.currentUser?.uid
            val order = Order(
                userId = userId.toString(),
                email = userEmail,
                address = et_address.text.toString(),
                username = username,
                product = product,
                date = date,
                time = time,
                price = price,
            )
            // Save the order to Firestore
            FirestoreClass().placeOrder(order,
                onSuccess = {
                    // Order placed successfully
                    hideProgressDialog()
                    val homeIntent = Intent(this, MainActivity::class.java)
                    bookingButton.setText("Submited")
                    bookingButton.isFocusable = false
                    bookingButton.isFocusableInTouchMode = false
                    // Clear cart items
                    FirestoreClass().clearCartItems(username,
                        onSuccess = {
                            // Cart items cleared successfully
                            Log.i("LabTestBookActivty", "Cart items cleared successfully")

                            // Clear input fields
                            uploadName.text.clear()
                            et_address.text.clear()
                            et_email.text.clear()
                            et_fees.setText("") // Clear the fees field
                        },
                        onFailure = { e ->
                            // Handle failure
                            Log.e("LabTestBookActivty", "Error clearing cart items", e)
                            Toast.makeText(this, "Failed to clear cart. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    )
                    startActivity(homeIntent)
                },
                onFailure = { e ->
                    // Handle failure
                    Log.e("LabTestBookActivty", "Error placing order", e)
                    Toast.makeText(this, "Failed to place booking. Please try again.", Toast.LENGTH_SHORT).show()
                }
            )
        }
backbtn.setOnClickListener{
    val backintent = Intent( this, LabTestDetailsActivity::class.java)
    startActivity(backintent)
}
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}