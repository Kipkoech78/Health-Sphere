package com.example.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.healthsphere.R
import com.example.models.Order

class OrdersAdapter(context: Context, orders: List<Order>) : ArrayAdapter<Order>(context, 0, orders) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val order = getItem(position) ?: return convertView ?: View(parent.context)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.order_items_acc, parent, false)

        val productTextView = view.findViewById<TextView>(R.id.productTextView)
        val priceTextView = view.findViewById<TextView>(R.id.priceTextView)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
        val timeTextView = view.findViewById<TextView>(R.id.timeTextView)

        productTextView.text = order.product
        priceTextView.text = order.price
        dateTextView.text = order.date
        timeTextView.text = order.time

        return view
    }
}
