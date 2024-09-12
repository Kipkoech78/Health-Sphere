package com.example.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.models.Appointment
import com.example.models.Order
import com.example.models.DoctorBooking
import com.example.firestore.FirestoreClass
import com.example.healthsphere.DoctorDashboardActivity
import com.example.healthsphere.R
import com.example.models.CartItem
import com.google.rpc.context.AttributeContext.Resource
class CombinedAdapter(private val context: Context, private var items: MutableList<Any>) : BaseAdapter() {

    private val firestoreManager = FirestoreClass()

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.multilines2, parent, false)
            viewHolder = ViewHolder(
                line1 = view.findViewById(R.id.line1),
                line2 = view.findViewById(R.id.line2),
                line3 = view.findViewById(R.id.line3),
                line4 = view.findViewById(R.id.line4),
                line5 = view.findViewById(R.id.line5),
                line6 = view.findViewById(R.id.line6),
                deleteButton = view.findViewById(R.id.deleteButton)
            )
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        // Fill view with data from either Order or DoctorBooking
        when (item) {
            is Order -> {
                viewHolder.deleteButton.isVisible = false
                viewHolder.line1.text = item.product
                viewHolder.line2.text = "PickUp station: ${item.address}"
               // viewHolder.line6.text = "Email: ${item.quantity}"
               viewHolder.line4.text = "Date: ${item.date}"
                viewHolder.line5.text = "Price: ${item.price} ksh"
              viewHolder.line3.text = "Time: ${item.time}"
            }
            is DoctorBooking -> {
                viewHolder.deleteButton
                viewHolder.line1.text = "Doctor: ${item.name}"
                viewHolder.line2.text = "Doctor Location: ${item.address}"
                viewHolder.line6.text = "Email: ${item.email}"
                viewHolder.line4.text = "Date: ${item.date}"
                viewHolder.line5.text = "Fees: ${item.fees} ksh"
                viewHolder.line3.text = "Time: ${item.time}"
            }
        }
        viewHolder.deleteButton.setOnClickListener {
            if (item is Appointment) {
                when (item.status) {
                    "Active", "Ongoing" -> {
                        // Cancel the appointment
                        firestoreManager.updateAppointmentStatus(
                            appointmentId = item.id,
                            newStatus = "Cancelled",
                            onSuccess = {
                                // Update local item status
                                item.status = "Cancelled"
                                Toast.makeText(context, "Appointment cancelled.", Toast.LENGTH_SHORT).show()
                                // Update the button text, color and refresh the adapter view
                                viewHolder.deleteButton.text = "Cancelled"
                                viewHolder.deleteButton.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark)) // Set button background to red
                                viewHolder.deleteButton.setTextColor(Color.WHITE) // Set text color to white for better visibility
                                notifyDataSetChanged()

                                // Optionally, notify the dashboard about the update
                                // (context as? DoctorDashboardActivity)?.onAppointmentStatusChanged()
                            },
                            onFailure = { e ->
                                e.printStackTrace()
                                Toast.makeText(context, "Failed to cancel the appointment.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    "Complete" -> {
                        // Handle completed appointment deletion
                        firestoreManager.deleteAppointment(
                            appointmentId = item.id,
                            onSuccess = {
                                Toast.makeText(context, "Appointment deleted.", Toast.LENGTH_SHORT).show()
                                items.remove(item)
                                notifyDataSetChanged()

                                // Optionally, notify the dashboard about the update
                                // (context as? DoctorDashboardActivity)?.onAppointmentStatusChanged()
                            },
                            onFailure = { e ->
                                e.printStackTrace()
                                Toast.makeText(context, "Failed to delete the appointment.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }else{
                //TODO delete Order or track order
            }
        }
        return view
    }
    private data class ViewHolder(
        val line1: TextView,
        val line2: TextView,
        val line3: TextView,
        val line4: TextView,
        val line5: TextView,
        val line6: TextView,
        val deleteButton: Button
    )
}
