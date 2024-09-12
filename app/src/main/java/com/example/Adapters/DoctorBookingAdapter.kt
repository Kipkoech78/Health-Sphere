package com.example.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.models.DoctorBooking
import com.example.healthsphere.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter



class DoctorBookingAdapter(private val context: Context, private val items: List<DoctorBooking>) : BaseAdapter() {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun getCount(): Int {
        // Return 1 when the list is empty to show the "No appointments" message
        return if (items.isEmpty()) 1 else items.size
    }

    override fun getItem(position: Int): DoctorBooking? {
        // Return null when the list is empty
        return if (items.isEmpty()) null else items[position]
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.multilines, parent, false)
            viewHolder = ViewHolder(
                line1 = view.findViewById(R.id.line1),
                line2 = view.findViewById(R.id.line2),
                line3 = view.findViewById(R.id.line3),
                line4 = view.findViewById(R.id.line4),
                line5 = view.findViewById(R.id.line5),
                line6 = view.findViewById(R.id.line6)
            )
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        if (items.isEmpty()) {
            // Display "No appointments available" when there are no items
            viewHolder.line1.text = "No appointments available"
            viewHolder.line2.visibility = View.GONE
            viewHolder.line3.visibility = View.GONE
            viewHolder.line4.visibility = View.GONE
            viewHolder.line5.visibility = View.GONE
            viewHolder.line6.visibility = View.GONE
        } else {
            // Display appointment details when there are items
            val item = getItem(position)!!
            viewHolder.line1.text = item.name
            viewHolder.line2.text = item.address
            viewHolder.line3.text = "Email: ${item.email}"
            viewHolder.line4.text = "Date: ${item.date}"
            viewHolder.line5.text = "Fees: ${item.fees} ksh"

            // Parse start time
            val startTime = LocalTime.parse(item.time, timeFormatter)
            // Calculate end time by adding 1 hour
            val endTime = startTime.plusHours(1)
            // Format and display the time range
            viewHolder.line6.text = "${startTime.format(timeFormatter)} to ${endTime.format(timeFormatter)}"

            // Make sure all lines are visible when there are items
            viewHolder.line2.visibility = View.VISIBLE
            viewHolder.line3.visibility = View.VISIBLE
            viewHolder.line4.visibility = View.VISIBLE
            viewHolder.line5.visibility = View.VISIBLE
            viewHolder.line6.visibility = View.VISIBLE
        }

        return view
    }

    private data class ViewHolder(
        val line1: TextView,
        val line2: TextView,
        val line3: TextView,
        val line4: TextView,
        val line5: TextView,
        val line6: TextView
    )
}
