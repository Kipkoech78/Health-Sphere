package com.example.healthsphere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firestore.FirestoreClass
import com.example.models.CartItem
import com.example.utils.Constants

class LabTestDetailsActivity : BazeActivity() {
    private lateinit var tv: TextView
    private lateinit var ed2: EditText
    private lateinit var totalCost: TextView
    private lateinit var cart_tv: TextView
    private lateinit var backbtn: LinearLayout
    private lateinit var firestoreClass: FirestoreClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lab_test_details)

        // Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize views
        tv = findViewById(R.id.tvTittle)
        ed2 = findViewById(R.id.lvtestDetails)
        totalCost = findViewById(R.id.totalCost)
        backbtn = findViewById(R.id.backbtn)
        cart_tv = findViewById(R.id.cart_tv)
        firestoreClass = FirestoreClass()

        // Set focusability properties for ed2
        ed2.isFocusable = false
        ed2.isFocusableInTouchMode = false

        // Retrieve data from the intent
        val intent = intent
        val text1 = intent.getStringExtra("text1") ?: "No title"
        val text2 = intent.getStringExtra("text2") ?: "No details"
        val x5 = intent.getStringExtra("x5") ?: "0"

        // Set text views with the retrieved data
        tv.text = text1
        ed2.setText(text2)
        totalCost.text = "Total Cost: $x5/-"

        // Set up back button click listener
        backbtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
//            val backIntent = Intent(this, LabTestActivity::class.java)
//            startActivity(backIntent)
        }

        // Set up cart button click listener
        cart_tv.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, Context.MODE_PRIVATE)
            val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
            val cartItem = CartItem(
                username = username,
                product = text1,
                price = x5.toFloat(),
                otype = text2
            )
            firestoreClass.checkCartItemExists(username, text1,
                onSuccess = { exists ->
                    if (exists) {
                        hideProgressDialog()
                        Log.i("LabTestDetailsActivity", "Item already exists in cart")
                        Toast.makeText(this, "Item already exist in the cart", Toast.LENGTH_SHORT).show()
                        // Optionally show a message to the user that the item is already in the cart
                    } else {
                        firestoreClass.addCartItem(cartItem,
                            onSuccess = {
                                hideProgressDialog()
                                Log.i("LabTestDetailsActivity", "Item added to cart successfully")
                                // Optionally show a confirmation message to the user
                                Toast.makeText(this, "Item successfully added to Cat", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { e ->
                                hideProgressDialog()
                                Log.e("LabTestDetailsActivity", "Failed to add item to cart", e)
                                // Optionally show an error message to the user
                            }
                        )
                    }
                },
                onFailure = { e ->
                    hideProgressDialog()
                    Log.e("LabTestDetailsActivity", "Failed to check if item exists in cart", e)
                    // Optionally show an error message to the user
                }
            )
        }
        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
