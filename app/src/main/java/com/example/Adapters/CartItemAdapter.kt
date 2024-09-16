package com.example.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.example.drugs.MedicineCartFragment
import com.example.healthsphere.R
import com.example.sqlite.CartDatabaseHelper
import com.example.sqlite.CartItem

class CartItemAdapter(
    private val context: Context,
    private val cartItems: List<CartItem>
) : ArrayAdapter<CartItem>(context, 0, cartItems) {

    private val dbHelper = CartDatabaseHelper(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position) ?: return convertView ?: View(context)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.cartmed_item_layout, parent, false)

        val drugNameTextView: TextView = view.findViewById(R.id.drugName)
        val priceTextView: TextView = view.findViewById(R.id.price)
        val btnBuy: Button = view.findViewById(R.id.btnBuy)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)

        drugNameTextView.text = item.drugName
        priceTextView.text = "Ksh: ${item.price}"

        btnBuy.setOnClickListener {
            // Handle buy action for this item
            handleBuy(item)
        }

        btnDelete.setOnClickListener {
            // Handle delete action for this item
            handleDelete(item)
        }

        return view
    }

    private fun handleBuy(item: CartItem) {
        Toast.makeText(context, "Bought ${item.drugName}", Toast.LENGTH_SHORT).show()
        // Implement your buy logic here
    }

    private fun handleDelete(item: CartItem) {
        AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete ${item.drugName}?")
            .setPositiveButton("Yes") { _, _ ->
                dbHelper.deleteCartItem(item.id)
                (context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.cartFragment)?.let {
                    (it as MedicineCartFragment).refreshCart()
                }
                Toast.makeText(context, "${item.drugName} deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
