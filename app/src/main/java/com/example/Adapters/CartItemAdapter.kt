package com.example.Adapters

import android.content.Context
import android.content.Intent
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
import com.example.drugs.OrderMedicineActivity
import com.example.healthsphere.R
import com.example.models.MedicineCartItem
import com.google.firebase.firestore.FirebaseFirestore

class CartItemAdapter(
    private val context: Context,
    private val cartItems: List<MedicineCartItem>
) : ArrayAdapter<MedicineCartItem>(context, 0, cartItems) {

    private val db = FirebaseFirestore.getInstance()

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

    private fun handleBuy(item: MedicineCartItem) {
       // Toast.makeText(context, "Bought ${item.drugName}", Toast.LENGTH_SHORT).show()

        // Create an intent to start the OrderMedicineActivity
        val intent = Intent(context, OrderMedicineActivity::class.java).apply {
            putExtra("price", item.price)          // Pass the price of the item
            putExtra("medicine_Name", item.drugName) // Pass the name of the medicine
        }

        // Start the OrderMedicineActivity
        context.startActivity(intent)
    }

    private fun handleDelete(item: MedicineCartItem) {
        AlertDialog.Builder(context)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete ${item.drugName}?")
            .setPositiveButton("Yes") { _, _ ->
                deleteItemFromFirestore(item.id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteItemFromFirestore(itemId: String) {
        db.collection("medicinecart").document(itemId)
            .delete()
            .addOnSuccessListener {
                (context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.cartFragment)?.let {
                    (it as MedicineCartFragment).refreshCart()
                }
                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete item", Toast.LENGTH_SHORT).show()
            }
    }
}
