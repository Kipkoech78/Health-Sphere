package com.example.healthsphere

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.Adapters.AppointmentAdapter
import com.example.models.Appointment
import com.example.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DoctorDashboardActivity : BazeActivity() {
    private lateinit var tvTotalFees: TextView
    private lateinit var lvAppointments: ListView
    private lateinit var backbtn: LinearLayout
    private val db = FirebaseFirestore.getInstance()
    private val handler = Handler()
    private val refreshInterval: Long = 60000 // 60 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_dashboard)

        tvTotalFees = findViewById(R.id.tvTotalFees)
        lvAppointments = findViewById(R.id.lvAppointments)
        backbtn = findViewById(R.id.backbtn)
        backbtn.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

        // Fetch appointments and update UI
        fetchAppointmentsForDoctor()
        showProgressDialog(resources.getString(R.string.loading))

        // Schedule periodic updates
        handler.postDelayed(object : Runnable {
            override fun run() {
                fetchAppointmentsForDoctor()
                handler.postDelayed(this, refreshInterval)
            }
        }, refreshInterval)
    }

    private fun fetchAppointmentsForDoctor() {
        val doctorEmail = getLoggedInDoctorEmail()
        val currentTime = System.currentTimeMillis()

        db.collection("appointments")
            .whereEqualTo("email", doctorEmail)
            .whereEqualTo("completed", false)
            .get()
            .addOnSuccessListener { documents ->
                hideProgressDialog()
                var totalFees = 0
                val appointments = mutableListOf<Appointment>()

                for (document in documents) {
                    val appointment = document.toObject(Appointment::class.java)
                    appointment.id = document.id // Set the document ID to the appointment object
                    val appointmentTimestamp = convertToTimestamp(appointment.date, appointment.time)

                    // Define the time bounds for the appointment (1 hour window)
                    val appointmentEndTime = appointmentTimestamp + 3600000 // 1 hour in milliseconds

                    when {
                        currentTime < appointmentTimestamp -> {
                            // Appointment has not started (Active)
                            appointment.status = "Active"
                        }
                        currentTime in appointmentTimestamp..appointmentEndTime -> {
                            // Appointment is ongoing
                            appointment.status = "Ongoing"
                        }
                        else -> {
                            appointment.status = "Complete"
                        }
//                        currentTime > appointmentEndTime -> {
//                            // Appointment is complete
//                            appointment.status = "Complete"
//                            markAppointmentComplete(document.id)
//                        }
                    }
                    // Add appointment to the list
                    appointments.add(appointment)

                    if (appointment.status != "Complete") {
                        appointments.add(appointment)
                        totalFees += appointment.fees.toIntOrNull() ?: 0
                    }
                }

                tvTotalFees.text = "Total Fees: Ksh $totalFees"
                val adapter = AppointmentAdapter(this, appointments)
                lvAppointments.adapter = adapter

                // Set a long click listener to delete completed appointments
                lvAppointments.setOnItemLongClickListener { parent, view, position, id ->
                    val appointment = appointments[position]
                    if (appointment.status == "Complete") {
                        deleteAppointment(appointment.id)
                    } else {
                        Toast.makeText(this, "Cannot delete active or ongoing appointments", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()
                Log.w("FirestoreError", "Error getting documents: ", exception)
            }
    }


    private fun deleteAppointment(appointmentId: String) {
        db.collection("appointments").document(appointmentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Appointment deleted successfully", Toast.LENGTH_SHORT).show()
                fetchAppointmentsForDoctor() // Refresh the list
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error deleting document", e)
            }
    }
    //    // Mark appointment as complete
    private fun markAppointmentComplete(appointmentId: String) {
        db.collection("appointments").document(appointmentId)
            .update("completed", true)
            .addOnSuccessListener {
                Log.d("FirestoreSuccess", "Appointment marked as complete.")
                fetchAppointmentsForDoctor() // Refresh appointments
            }
            .addOnFailureListener { e ->
                Log.w("FirestoreError", "Error updating document", e)
            }
    }

    // Function to convert appointment date and time to timestamp
    private fun convertToTimestamp(date: String, time: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateTime = "$date $time"
        return try {
            val parsedDate = dateFormat.parse(dateTime)
            parsedDate?.time ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    private fun getLoggedInDoctorEmail(): String {
        val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, MODE_PRIVATE)
        return sharedPreferences.getString(Constants.LOGGED_IN_USEREMAIL, "") ?: ""
    }
}


//package com.example.healthsphere
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import android.widget.LinearLayout
//import android.widget.ListView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.Adapters.AppointmentAdapter
//import com.example.models.Appointment
//import com.example.utils.Constants
//import com.google.firebase.firestore.FirebaseFirestore
//import java.text.SimpleDateFormat
//import java.util.*
//
//class DoctorDashboardActivity : AppCompatActivity() {
//    private lateinit var tvTotalFees: TextView
//    private lateinit var lvAppointments: ListView
//    private lateinit var backbtn: LinearLayout
//    private val db = FirebaseFirestore.getInstance()
//    private val handler = Handler()
//    private val refreshInterval: Long = 60000 // 60 seconds
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_doctor_dashboard)
//
//        tvTotalFees = findViewById(R.id.tvTotalFees)
//        lvAppointments = findViewById(R.id.lvAppointments)
//        backbtn = findViewById(R.id.backbtn)
//        backbtn.setOnClickListener {
//            val backIntent = Intent(this, MainActivity::class.java)
//            startActivity(backIntent)
//        }
//
//        // Fetch appointments and update UI
//        fetchAppointmentsForDoctor()
//
//        // Schedule periodic updates
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                fetchAppointmentsForDoctor()
//                handler.postDelayed(this, refreshInterval)
//            }
//        }, refreshInterval)
//    }
//
//    private fun fetchAppointmentsForDoctor() {
//
//        val doctorEmail = getLoggedInDoctorEmail()
//        val currentTime = System.currentTimeMillis()
//
//        db.collection("appointments")
//            .whereEqualTo("email", doctorEmail)
//            .whereEqualTo("completed", false) // Only fetch incomplete appointments
//            .get()
//            .addOnSuccessListener { documents ->
//                var totalFees = 0
//                val appointments = mutableListOf<Appointment>()
//
//                for (document in documents) {
//                    val appointment = document.toObject(Appointment::class.java)
//                    appointment.id = document.id // Set the document ID to the appointment object
//                    val appointmentTimestamp = convertToTimestamp(appointment.date, appointment.time)
//                    if (currentTime > appointmentTimestamp) {
//                        markAppointmentComplete(document.id)
//                    } else {
//                        appointments.add(appointment)
//                        totalFees += appointment.fees.toIntOrNull() ?: 0
//                    }
//                }
//
//                tvTotalFees.text = "Total Fees: Ksh $totalFees"
//                val adapter = AppointmentAdapter(this, appointments)
//                lvAppointments.adapter = adapter
//
//                // Set a long click listener to delete completed appointments
//                lvAppointments.setOnItemLongClickListener { parent, view, position, id ->
//                    val appointment = appointments[position]
//                    if (appointment.completed) {
//                        deleteAppointment(appointment.id)
//                    } else {
//                        Toast.makeText(this, "Cannot delete active appointments", Toast.LENGTH_SHORT).show()
//                    }
//                    true
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("FirestoreError", "Error getting documents: ", exception)
//            }
//    }
//
//    private fun deleteAppointment(appointmentId: String) {
//        db.collection("appointments").document(appointmentId)
//            .delete()
//            .addOnSuccessListener {
//                Toast.makeText(this, "Appointment deleted successfully", Toast.LENGTH_SHORT).show()
//                fetchAppointmentsForDoctor() // Refresh the list
//            }
//            .addOnFailureListener { e ->
//                Log.w("FirestoreError", "Error deleting document", e)
//            }
//    }
//
//    // Function to convert appointment date and time to timestamp
//    private fun convertToTimestamp(date: String, time: String): Long {
//        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
//        val dateTime = "$date $time"
//        return try {
//            val parsedDate = dateFormat.parse(dateTime)
//            parsedDate?.time ?: 0L
//        } catch (e: Exception) {
//            e.printStackTrace()
//            0L
//        }
//    }
//
//
//    // Mark appointment as complete
//    private fun markAppointmentComplete(appointmentId: String) {
//        db.collection("appointments").document(appointmentId)
//            .update("completed", true)
//            .addOnSuccessListener {
//                Log.d("FirestoreSuccess", "Appointment marked as complete.")
//                fetchAppointmentsForDoctor() // Refresh appointments
//            }
//            .addOnFailureListener { e ->
//                Log.w("FirestoreError", "Error updating document", e)
//            }
//    }
//
//    private fun getLoggedInDoctorEmail(): String {
//        val sharedPreferences = getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, MODE_PRIVATE)
//        return sharedPreferences.getString(Constants.LOGGED_IN_USEREMAIL, "") ?: ""
//    }
//
//}
