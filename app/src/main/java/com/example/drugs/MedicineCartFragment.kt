package com.example.drugs

import android.content.Context
import android.content.SharedPreferences
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
import com.example.healthsphere.R
import com.example.models.MedicineCartItem
import com.example.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MedicineCartFragment : Fragment() {

    private lateinit var listViewCart: ListView
    private lateinit var cartItems: List<MedicineCartItem>
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

        // Initialize views
        listViewCart = view.findViewById(R.id.list_View_cart)
        btnBuyAll = view.findViewById(R.id.btnBuyAll)
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice)

        // Fetch cart items from Firestore
        refreshCart()

        // Handle "Buy All" button click
        btnBuyAll.setOnClickListener {
            handleBuyAll()
        }

        return view
    }
    fun refreshCart() {

        val sharedPreferences = requireContext().getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString(Constants.LOGGED_IN_USER_ID, "")!!

        if (userId != null) {
            Log.d("MedicineCartFragment", "Fetching cart items for user: $userId")

            // Access the medicine cart collection and filter by userId
            db.collection("medicinecart")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    Log.d("MedicineCartFragment", "Documents fetched successfully: ${documents.size()}")
                    cartItems = documents.toObjects(MedicineCartItem::class.java)

                    // Check if cartItems is populated
                    Log.d("MedicineCartFragment", "Cart Items: $cartItems")

                    // Set up the adapter
                    cartItemAdapter = CartItemAdapter(requireContext(), cartItems)
                    listViewCart.adapter = cartItemAdapter

                    // Calculate the total price
                    val totalPrice = cartItems.sumOf { it.price.toDouble() }
                    textViewTotalPrice.text = "Total Price: Ksh $totalPrice"
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("MedicineCartFragment", "No userId found in SharedPreferences")
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }


    //    fun refreshCart() {
//
//        currentUser?.uid?.let { userId ->
//            db.collection("users").document(userId).collection("medicinecart")
//                .get()
//                .addOnSuccessListener { documents ->
//                    cartItems = documents.toObjects(MedicineCartItem::class.java)
//
//                    // Set up the adapter
//                    cartItemAdapter = CartItemAdapter(requireContext(), cartItems)
//                    listViewCart.adapter = cartItemAdapter
//
//                    // Calculate the total price
//                    val totalPrice = cartItems.sumOf { it.price.toDouble() }
//                    textViewTotalPrice.text = "Total Price: Ksh $totalPrice"
//                }
//                .addOnFailureListener { e ->
//                    e.printStackTrace()
//                    Toast.makeText(requireContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show()
//                }
//        }
//    }
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
                completeTransaction(userId)
            }
            .addOnFailureListener { e ->
                Log.e("MedicineCartFragment", "Failed to update balance", e)
                Toast.makeText(requireContext(), "Failed to complete purchase", Toast.LENGTH_SHORT).show()
            }
    }

    private fun completeTransaction(userId: String) {
        // Clear the cart in Firestore after a successful purchase
        db.collection("users").document(userId).collection("medicinecart")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("users").document(userId)
                        .collection("medicinecart").document(document.id).delete()
                }
                refreshCart() // Refresh the cart UI after clearing it
            }
            .addOnFailureListener { e ->
                Log.e("MedicineCartFragment", "Failed to clear cart", e)
            }
    }
}
