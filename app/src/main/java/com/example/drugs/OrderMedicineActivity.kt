package com.example.drugs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.healthsphere.R
import com.example.models.medicineOrder
import com.example.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class OrderMedicineActivity : AppCompatActivity() {
    private lateinit var orderId: EditText
    private lateinit var  orderDrugButton: Button
    private lateinit var drugName : EditText
    private lateinit var  et_fees: EditText
    private lateinit var selectChemist : EditText
    private lateinit var dateButton : Button
    private lateinit var timeButton: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.order_medicine)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val sharedPreferences = getSharedPreferences(Constants.LOGGED_IN_USERNAME, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")
        orderId = findViewById(R.id.orderId)
        drugName = findViewById(R.id.drugName)
        et_fees = findViewById(R.id.et_fees)
        selectChemist = findViewById(R.id.selectChemist)
        timeButton = findViewById(R.id.time_et)
        val chemistNames = resources.getStringArray(R.array.chemist_names)
        selectChemist.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_chemist_search, null)
            dialog.setView(dialogView)
            // Set up the ListView in the dialog
            val listView: ListView = dialogView.findViewById(R.id.list_chemist)
            val searchView: SearchView = dialogView.findViewById(R.id.search_view_chemist)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, chemistNames)
            listView.adapter = adapter
            // Show the dialog
            val alertDialog = dialog.create()
            alertDialog.show()
            // Handle the search functionality
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return false
                }
            })
            // Handle list item clicks
            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedChemist = adapter.getItem(position)
                selectChemist.setText(selectedChemist)
                alertDialog.dismiss()
            }
        }
        et_fees.isFocusable = false
        et_fees.isFocusableInTouchMode = false
        orderId.isFocusable = false
        orderId.isFocusableInTouchMode = false
        drugName.isFocusable = false
        drugName.isFocusableInTouchMode = false
        dateButton.isVisible = true
        dateButton.isFocusable = false
        dateButton.isFocusableInTouchMode = false
        timeButton.isVisible = true

        timeButton.setOnClickListener {
            // Get the current time
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val currentTime = String.format(Locale.getDefault(), "%02d: %02d",hour, minute )
            timeButton.text = currentTime
        }

        dateButton = findViewById(R.id.dateEditText)


        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = String.format(Locale.getDefault(), "%02d/%02d/%d", currentDay, currentMonth + 1, currentYear)
        dateButton.text = currentDate
        dateButton.setOnClickListener {
            // Create a DatePickerDialog with the current date as default
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format the selected date
                    val date = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                    // Set the date to the Button's text
                    dateButton.text = date
                },
                currentYear, currentMonth, currentDay // Set initial date to current date
            )

            // Show the dialog
            datePickerDialog.show()
        }
        orderDrugButton = findViewById(R.id.orderDrugButton)
        val price = intent.getStringExtra("price") ?: ""
        val medicineName = intent.getStringExtra("medicine_Name") ?: ""
        et_fees.setText(price)
        drugName.setText(medicineName)


        orderDrugButton.setOnClickListener {
            val userId = auth.currentUser?.uid
            val selectedDrugName = drugName.text.toString()
            val selectedChemist = selectChemist.text.toString()
            val fees = et_fees.text.toString()
            val time = timeButton.text.toString()
            val date = dateButton.text.toString()
            if (userId != null) {
                checkIfOrderExists(userId, selectedDrugName, time, date, fees, selectedChemist)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkIfOrderExists(userId: String,
                                   drugName: String,
                                   time: String,
                                   date: String,
                                   fees: String,
                                   chemist: String) {
        firestore.collection("medicine_orders")
            .whereEqualTo("userId", userId)
            .whereEqualTo("drugName", drugName)
            .whereEqualTo("time", time)
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty){
                    saveOrderToFirestore(userId, drugName, time, date, fees, chemist)

                } else {
                    Toast.makeText(this, " order already Exist ", Toast.LENGTH_SHORT).show()

                }
             }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error Checking Order..  ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun saveOrderToFirestore(userId: String,
                                     drugName: String,
                                     time: String,
                                     date: String,
                                     fees: String,
                                     chemist: String) {
        // Generate a unique order ID using Firestore's auto-generated document ID
        val orderRef = firestore.collection("medicines_orders").document()
        val medicineOrder = medicineOrder(
            orderId = orderRef.id,
            userId = userId,
            drugName = drugName,
            time = time,
            date = date,
            fees = fees,
            chemist = chemist
        )
        orderRef.set(medicineOrder)
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed " +
                        " at ${time} " +
                        "successfully! Your Order ID: ${orderRef.id}", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener {
                e -> Toast.makeText(this, "Failed to place Order : ${e.message}", Toast.LENGTH_SHORT)
                .show()
            }
    }
}