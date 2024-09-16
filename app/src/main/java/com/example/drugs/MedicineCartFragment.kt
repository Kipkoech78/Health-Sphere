package com.example.drugs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.Adapters.CartItemAdapter
import com.example.healthsphere.BazeActivity
import com.example.healthsphere.R
import com.example.sqlite.CartDatabaseHelper
import com.example.sqlite.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MedicineCartFragment : Fragment() {

    private lateinit var dbHelper: CartDatabaseHelper
    private lateinit var listViewCart: ListView
    private lateinit var cartItems: List<CartItem>
    private lateinit var cartItemAdapter: CartItemAdapter
    private lateinit var textViewTotalPrice: TextView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var btnBuyAll: Button
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_medicine_cart, container, false)
        dbHelper = CartDatabaseHelper(requireContext())

        // Initialize views
        listViewCart = view.findViewById(R.id.list_View_cart)
        btnBuyAll = view.findViewById(R.id.btnBuyAll)
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice)
        refreshCart()
        btnBuyAll.setOnClickListener {
            handleBuyAll()
        }

        return view
    }

    fun refreshCart() {
        cartItems = dbHelper.getAllCartItems()
        cartItemAdapter = CartItemAdapter(requireContext(), cartItems)
        listViewCart.adapter = cartItemAdapter

        val totalPrice = cartItems.sumOf { it.price.toDouble() }
        textViewTotalPrice.text = "Total Price: Ksh $totalPrice"
    }
    private fun handleBuyAll() {
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val userBalance = document.getLong("balance")?.toInt() ?: 0
                    val totalPrice = cartItems.sumOf { it.price.toDouble() }.toInt()

                    if (userBalance >= totalPrice) {
                        // If the user has enough balance, proceed with the purchase
                        processPurchase(userId, totalPrice)
                    } else {
                        // Show insufficient balance message
                        Toast.makeText(requireContext(), "Insufficient balance. You need Ksh $totalPrice but you only have Ksh $userBalance.", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed to retrieve user balance", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun processPurchase(userId: String, totalPrice: Int) {
        Log.d("MedicineCartFragment", "Processing purchase for user: $userId, Total Price: $totalPrice")

        // Deduct balance from the user's account in Firestore
        db.collection("users").document(userId)
            .update("balance", FieldValue.increment(-totalPrice.toLong()))
            .addOnSuccessListener {
                Log.d("MedicineCartFragment", "Balance updated successfully")
                Toast.makeText(requireContext(), "Purchase successful", Toast.LENGTH_SHORT).show()
                completeTransaction()
            }
            .addOnFailureListener { e ->
                Log.e("MedicineCartFragment", "Failed to update balance", e)
                Toast.makeText(requireContext(), "Failed to complete purchase", Toast.LENGTH_SHORT).show()
            }
    }

    private fun completeTransaction() {
        // Clear the cart and refresh the cart UI after a successful purchase
        dbHelper.clearCart()
        refreshCart()
    }


}
