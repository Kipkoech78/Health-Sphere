package com.example.healthsphere

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.Adapters.CombinedAdapter
import com.example.Adapters.OrderAdapter
import com.example.firestore.FirestoreClass
import com.example.models.Order
import com.example.models.DoctorBooking
import com.example.utils.Constants

class OrderDetActivity : AppCompatActivity() {
    private lateinit var backbtn: LinearLayout
    private lateinit var tvTittle: TextView
    private lateinit var lvOrderDetails: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_det)
        lvOrderDetails = findViewById(R.id.lvOrderDetails)
        // Fetch order details and appointments from Firestore
        loadOrderDetailsAndAppointments()
        backbtn = findViewById(R.id.backbtn)
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
    private fun loadOrderDetailsAndAppointments() {
        tvTittle = findViewById(R.id.tvTittle)
        val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "") ?: ""
        tvTittle.text = "Thank You ${username}. Orders and Appointments received"
        // Fetch orders
        FirestoreClass().getOrdersForUser(username, onSuccess = { orders ->
            // Fetch appointments after fetching orders
            FirestoreClass().getDoctorBookingsForUser(username, onSuccess = { bookings ->
                // Combine orders and bookings into a single list
                val combinedList = mutableListOf<Any>().apply {
                    addAll(orders)
                    addAll(bookings)
                }
                setupListView(combinedList)
            }, onFailure = { e ->
                Toast.makeText(this, "Error loading appointments: ${e.message}", Toast.LENGTH_SHORT).show()
            })
        }, onFailure = { e ->
            Toast.makeText(this, "Error loading orders: ${e.message}", Toast.LENGTH_SHORT).show()
        })
    }
    private fun setupListView(items: List<Any>) {
       //val adapter = CombinedAdapter(this, items)
        val mutableItems = items.toMutableList()
        val adapter = CombinedAdapter(this, mutableItems)
        lvOrderDetails.adapter = adapter
    }
}
