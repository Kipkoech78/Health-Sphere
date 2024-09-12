package com.example.healthsphere

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firestore.FirestoreClass
import com.example.models.CartItem
import com.example.utils.Constants
import java.util.Calendar
import java.util.Locale

class CatLabActivity : AppCompatActivity() {
    private lateinit var lvtestDetails: EditText
    private lateinit var tvTittle: TextView
    private lateinit var totalCost: TextView
    private lateinit var time_et: Button
    private lateinit var backbtn: LinearLayout
    private lateinit var cartCheckout: TextView
    private lateinit var dateEditText: Button
    private val firestoreClass = FirestoreClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cat_lab)

        // Initialize views
        backbtn = findViewById(R.id.backbtn)
        lvtestDetails = findViewById(R.id.lvtestDetails)
        tvTittle = findViewById(R.id.tvTittle)
        cartCheckout = findViewById(R.id.cartCheckout)
        dateEditText = findViewById(R.id.dateEditText)
        totalCost = findViewById(R.id.totalCost)
        time_et = findViewById(R.id.time_et)

        lvtestDetails.isFocusable = false
        lvtestDetails.isFocusableInTouchMode = false

        val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        tvTittle.setText(username)

        // Load cart items
        loadCartItems(username)

        // Set up time picker and date picker
        setupTimePicker()
        setupDatePicker()

        backbtn.setOnClickListener {
            val backintent = Intent(this, LabTestActivity::class.java)
            startActivity(backintent)
        }
        cartCheckout.setOnClickListener{
            val priceText = totalCost.text.toString().trim()  // Remove "Ksh" and "Total:" from the text
            val priceWithCurrency = "${priceText}"  // Add "Ksh " prefix to the price
            val bookIntent = Intent(this, LabTestBookActivty::class.java)
            bookIntent.putExtra("packageName", lvtestDetails.text.toString())
            bookIntent.putExtra("price", priceWithCurrency)  // Pass the price with currency
            bookIntent.putExtra("date", dateEditText.text.toString())  // Pass the selected date
            bookIntent.putExtra("time", time_et.text.toString())  // Pass the selected time
            startActivity(bookIntent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun loadCartItems(username: String) {
        firestoreClass.getCartItemsByUser(username,
            onSuccess = { cartItems ->
                displayCartItems(cartItems)
            },
            onFailure = { e ->
                Log.e("CatLabActivity", "Error loading cart items", e)
            }
        )
    }
    private fun displayCartItems(cartItems: List<CartItem>) {
        var totalCostValue = 0f
        // Loop through cart items and display them
        cartItems.forEach { cartItem ->
            // Display cart item details (you can customize how to display each item)
            lvtestDetails.append("${cartItem.product}: ${cartItem.price}\n ")

            // Calculate total cost
            totalCostValue += cartItem.price
        }
        // Display total cost
        totalCost.text = String.format(Locale.getDefault(), "Total: \$%.2f", totalCostValue)
    }
    private fun setupTimePicker() {
        time_et.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                    val time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                    time_et.text = time
                },
                hour, minute, true
            )
            timePickerDialog.show()
        }
    }
    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = String.format(Locale.getDefault(), "%02d/%02d/%d", currentDay, currentMonth + 1, currentYear)
        dateEditText.text = currentDate

        dateEditText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val date = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                    dateEditText.text = date
                },
                currentYear, currentMonth, currentDay
            )
            datePickerDialog.show()
        }
    }
}