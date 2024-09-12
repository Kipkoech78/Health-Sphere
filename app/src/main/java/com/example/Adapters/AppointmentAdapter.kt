package com.example.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.models.Appointment
import com.example.healthsphere.R

class AppointmentAdapter(private val context: Context, private val appointments: List<Appointment>) :
        ArrayAdapter<Appointment>(context, 0, appointments) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false)
        val appointment = getItem(position)

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val appointment = getItem(position)
//        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false)

        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)
        val tvFees = view.findViewById<TextView>(R.id.tvFees)
        val Email = view.findViewById<TextView>(R.id.appointeeEmail)
        val tvName = view.findViewById<TextView>(R.id.docName)
        val tvStatus = view.findViewById<TextView>(R.id.tvAppointmentStatus)
        val username  = view.findViewById<TextView>(R.id.appointee)
        Email.text = "Patient's Email: ${appointment?.userEmail}"
        tvName.text = "Doc: ${appointment?.name}"
        tvDate.text = "Date: ${appointment?.date}"
        tvTime.text = "Time: ${appointment?.time}"
        tvFees.text = "Fees: Ksh ${appointment?.fees}"
        username.text = "Patient Name: ${appointment?.username}"
        // Set the status text and background color based on the appointment status
        when (appointment?.status) {
            "Active" -> {
                tvStatus.text = "Active"
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark))
            }
            "Ongoing" -> {
                tvStatus.text = "Ongoing"
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark))
            }
            "Complete" -> {
                tvStatus.text = "Completed"
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            }
            else -> {
                tvStatus.text = "Cancelled"
                tvStatus.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }
        }


        return view
    }
}
