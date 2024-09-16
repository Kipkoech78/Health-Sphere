package com.example.healthsphere

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BookActivity : BazeActivity() {
    private lateinit var backbtn: LinearLayout
    private lateinit var tv: TextView
    private lateinit var  appointmentButton: Button
    private lateinit var ed1: EditText
    private lateinit var ed2: EditText
    private lateinit var ed3: EditText
    private lateinit var ed4 : EditText
    private lateinit var timeButton: Button
    private lateinit var dateButton: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book)
        backbtn= findViewById(R.id.backbtn)
        tv = findViewById(R.id.tv_title)
        ed1 = findViewById(R.id.uploadEmail)
        ed2 = findViewById(R.id.et_address)
        ed3 = findViewById(R.id.et_email)
        ed4 = findViewById(R.id.et_fees)
        //initiate auth
        auth = FirebaseAuth.getInstance()
        appointmentButton = findViewById(R.id.appointmentButton)
        timeButton = findViewById(R.id.time_et)
        timeButton.setOnClickListener {
            // Get the current time
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            // Create a TimePickerDialog
            val timePickerDialog = TimePickerDialog(
                this,
                { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                    // Format the selected time
                    val time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                    // Set the time to the Button's text
                    timeButton.text = time
                },
                hour, minute, true // Use 24-hour format
            )
            // Show the dialog
            timePickerDialog.show()
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


        ed1.isFocusable = false
        ed1.isFocusableInTouchMode = false
        ed2.isFocusable = false
        ed2.isFocusableInTouchMode = false
        ed3.isFocusable = false
        ed3.isFocusableInTouchMode = false
        ed4.isFocusable = false
        ed4.isFocusableInTouchMode = false

// Get Intent and extract data
        val intent = intent
        val name = intent.getStringExtra("text1") ?: ""
        val email = intent.getStringExtra("text2") ?: ""
        val address = intent.getStringExtra("text3") ?: ""
//        val exp = intent.getStringExtra("text4") ?: ""
        val fees = intent.getStringExtra("text5") ?: ""

// Set the extracted data to the views
        tv.text = name
        ed1.setText(email)
        ed2.setText(address)
//        ed3.setText(exp)
        ed4.setText(fees)
        backbtn.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
//            val backintent = Intent(this, FindDoctors::class.java)
//            startActivity(backintent)
        }


//        appointmentButton.setOnClickListener {
//            showProgressDialog(resources.getString(R.string.booking))
//            val userId = auth.currentUser ?.uid
//            // Collect appointment details
//            val name = tv.text.toString()
//            val email = ed1.text.toString()
//            val address = ed2.text.toString()
////            val exp = ed3.text.toString()
//            val fees = ed4.text.toString()
//            val date = dateButton.text.toString()
//            val time = timeButton.text.toString()
//            val selectedDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse("$date $time")
//            val currentDateTime = Calendar.getInstance().time
//            if(selectedDateTime.before(currentDateTime)){
//                hideProgressDialog()
//                Toast.makeText(this, "Selected time is in the past. Please choose a valid time.", Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            }
//            // Get username from shared preferences
//            val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, MODE_PRIVATE)
//            val useremail = sharedPreferences.getString(Constants.LOGGED_IN_USEREMAIL, "") ?: ""
//            val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "") ?: ""
//            // Check for existing appointments
//            val appointmentQuery = db.collection("appointments")
//                .whereEqualTo("date", date)
//                .whereEqualTo("time", time)
//
//            appointmentQuery.get()
//                .addOnSuccessListener { querySnapshot ->
//                    hideProgressDialog()
//                    if (querySnapshot.isEmpty) {
//                        val active = selectedDateTime.after(currentDateTime)
//                        // No existing appointments, proceed with adding the new one
//                        val appointment = hashMapOf(
//                            "name" to name,
//                            "email" to email,
//                            "address" to address,
//                            "userId" to userId,
////                            "exp" to exp,
//                            "active" to active,
//                            "fees" to fees,
//                            "date" to date,
//                            "time" to time,
//                            "username" to username,
//                            "userEmail" to useremail,
//                            "completed" to false
//                        )
//                        db.collection("appointments")
//                            .add(appointment)
//                            .addOnSuccessListener {
//
//                                // Handle success, maybe show a message
//                                val detIntent = Intent(this, OrderDetActivity::class.java)
//                                startActivity(detIntent)
//                                showProgressDialog(resources.getString(R.string.please_wait))
//                                Toast.makeText(this, "Appointment booked successfully", Toast.LENGTH_SHORT).show()
//                            }
//                            .addOnFailureListener {
//                                hideProgressDialog()
//                                // Handle failure, maybe show an error message
//                                Toast.makeText(this, "Failed to book appointment", Toast.LENGTH_SHORT).show()
//                            }
//                    } else {
//                        hideProgressDialog()
//                        // There is a conflict, show an alert to the user
//                        Toast.makeText(this, "The selected time slot is already booked. Please choose another time.", Toast.LENGTH_LONG).show()
//                    }
//                }
//                .addOnFailureListener {
//                    // Handle query failure
//                    showProgressDialog(resources.getString(R.string.booking))
//                    Toast.makeText(this, "Failed to check availability", Toast.LENGTH_SHORT).show()
//                }
//        }
        appointmentButton.setOnClickListener {
            showProgressDialog(resources.getString(R.string.booking))

            // Get appointment details
            val userId = auth.currentUser?.uid
            val name = tv.text.toString()
            val email = ed1.text.toString()
            val address = ed2.text.toString()
            val fees = ed4.text.toString().toIntOrNull() ?: 0 // Convert fees to Int
            val date = dateButton.text.toString()
            val time = timeButton.text.toString()

            val selectedDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse("$date $time")
            val currentDateTime = Calendar.getInstance().time

            if (selectedDateTime.before(currentDateTime)) {
                hideProgressDialog()
                Toast.makeText(this, "Selected time is in the past. Please choose a valid time.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Check for existing appointments
            val appointmentQuery = db.collection("appointments")
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)

            appointmentQuery.get()
                .addOnSuccessListener { querySnapshot ->
                    hideProgressDialog()

                    if (querySnapshot.isEmpty) {
                        // Check user's balance before booking
                        db.collection("users").document(userId!!).get()
                            .addOnSuccessListener { document ->
                                val userBalance = document.getLong("balance")?.toInt() ?: 0

                                if (userBalance >= fees) {
                                    // Show alert dialog to confirm payment
                                    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                                    builder.setTitle("Confirm Payment")
                                    builder.setMessage("The total amount to be deducted is Ksh $fees. Do you want to proceed?")
                                    builder.setPositiveButton("Yes") { _, _ ->
                                        // Deduct balance and book appointment
                                        processPayment(userId, fees, name, email, address, date, time, selectedDateTime)
                                    }
                                    builder.setNegativeButton("No") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    builder.create().show()
                                } else {
                                    // Insufficient funds
                                    Toast.makeText(this, "Insufficient balance. You need Ksh $fees but you only have Ksh $userBalance.", Toast.LENGTH_LONG).show()
                                }
                            }
                            .addOnFailureListener {
                                hideProgressDialog()
                                Toast.makeText(this, "Failed to retrieve user balance", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        hideProgressDialog()
                        Toast.makeText(this, "The selected time slot is already booked. Please choose another time.", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    hideProgressDialog()
                    Toast.makeText(this, "Failed to check availability", Toast.LENGTH_SHORT).show()
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun processPayment(userId: String, fees: Int, name: String, email: String, address: String, date: String, time: String, selectedDateTime: java.util.Date) {
        showProgressDialog(resources.getString(R.string.processing_payment))

        // Deduct user's balance
        db.collection("users").document(userId)
            .update("balance", FieldValue.increment(-fees.toLong()))
            .addOnSuccessListener {
                // Create the appointment in Firestore
                val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, MODE_PRIVATE)
                val useremail = sharedPreferences.getString(Constants.LOGGED_IN_USEREMAIL, "") ?: ""
                val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "") ?: ""
                val active = selectedDateTime.after(Calendar.getInstance().time)

                val appointment = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "address" to address,
                    "userId" to userId,
                    "fees" to fees.toString(),
                    "date" to date,
                    "time" to time,
                    "username" to username,
                    "userEmail" to useremail,
                    "completed" to false,
                    "active" to active
                )

                db.collection("appointments").add(appointment)
                    .addOnSuccessListener {
                        hideProgressDialog()
                        val detIntent = Intent(this, OrderDetActivity::class.java)
                        startActivity(detIntent)
                        Toast.makeText(this, "Appointment booked and payment successful", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        hideProgressDialog()
                        Toast.makeText(this, "Failed to book appointment", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                hideProgressDialog()
                Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show()
            }
    }

}