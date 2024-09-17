package com.example.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.models.medicineOrder
import com.example.healthsphere.R

class MedicineOrdersAdapter(private val context: Context, private val orders: List<medicineOrder>) : BaseAdapter() {

    override fun getCount(): Int {
        return orders.size
    }

    override fun getItem(position: Int): Any {
        return orders[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = convertView ?: inflater.inflate(R.layout.medicine_order_item, parent, false)

        val order = getItem(position) as medicineOrder

        val drugNameTextView = view.findViewById<TextView>(R.id.drugNameTextView)
        val feesTextView = view.findViewById<TextView>(R.id.feesTextView)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
        val timeTextView = view.findViewById<TextView>(R.id.timeTextView)
        val OrdersTextView = view.findViewById<TextView>(R.id.orderId)

        drugNameTextView.text = order.drugName
        feesTextView.text = "Fees: Ksh ${order.fees}"
        dateTextView.text = "Date: ${order.date}"
        timeTextView.text = "Time: ${order.time}"
        OrdersTextView.text = "Order Id: ${order.orderId}"

        return view
    }
}
