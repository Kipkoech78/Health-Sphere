package com.example.drugs

import android.app.AlertDialog
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
import com.example.models.medicineOrder
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
                        // Show confirmation dialog
                        showConfirmationDialog(userId, totalPrice)
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

    private fun showConfirmationDialog(userId: String, totalPrice: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Purchase")
            .setMessage("You are about to spend Ksh $totalPrice. Do you want to proceed?")
            .setPositiveButton("Yes") { dialog, _ ->
                // Proceed with the purchase
                processPurchase(userId, totalPrice)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                // Cancel the purchase
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun processPurchase(userId: String, totalPrice: Int) {
        Log.d("MedicineCartFragment", "Processing purchase for user: $userId, Total Price: $totalPrice")

        // Deduct balance from the user's account in Firestore
        db.collection("users").document(userId)
            .update("balance", FieldValue.increment(-totalPrice.toLong()))
            .addOnSuccessListener {
                Log.d("MedicineCartFragment", "Balance updated successfully")

                // Add purchased items to medicineOrders collection
                addItemsToMedicineOrders(userId, totalPrice)

                // Clear the cart
                completeTransaction(userId)
            }
            .addOnFailureListener { e ->
                Log.e("MedicineCartFragment", "Failed to update balance", e)
                Toast.makeText(requireContext(), "Failed to complete purchase", Toast.LENGTH_SHORT).show()
            }
    }
    private fun addItemsToMedicineOrders(userId: String, totalPrice: Int) {
        val medicineOrdersCollection = db.collection("medicines_orders") // Top-level collection

        for (item in cartItems) {
            // Generate a new document reference for each order
            val orderRef = medicineOrdersCollection.document()

            // Create an order object
            val order = medicineOrder(
                orderId = orderRef.id,  // Firestore will generate this document ID
                userId = userId,        // Store userId as a field
                drugName = item.drugName,
                time = getCurrentTime(),  // Implement getCurrentTime() to get the current time as a string
                date = getCurrentDate(),  // Implement getCurrentDate() to get the current date as a string
                fees = item.price,
                chemist = "" // Provide chemist info if applicable
            )

            // Add the order to the top-level medicines_orders collection
            orderRef.set(order)
                .addOnSuccessListener {
                    Log.d("MedicineCartFragment", "Item added to medicineOrders: ${item.drugName}")
                }
                .addOnFailureListener { e ->
                    Log.e("MedicineCartFragment", "Failed to add item to medicineOrders", e)
                }
        }
    }


    //    private fun addItemsToMedicineOrders(userId: String, totalPrice: Int) {
//        val medicineOrdersCollection = db.collection("medicines_orders").document(userId).collection("medicines_orders")
//
//        for (item in cartItems) {
//            val order = medicineOrder(
//                orderId = medicineOrdersCollection.document().id, // Firestore will automatically generate this
//                userId = userId,
//                drugName = item.drugName,
//                time = getCurrentTime(),  // Implement getCurrentTime() to get the current time as a string
//                date = getCurrentDate(),  // Implement getCurrentDate() to get the current date as a string
//                fees = item.price,
//                chemist = "" // Provide chemist info if applicable
//            )
//
//            medicineOrdersCollection.add(order)
//                .addOnSuccessListener {
//                    Log.d("MedicineCartFragment", "Item added to medicineOrders: ${item.drugName}")
//                }
//                .addOnFailureListener { e ->
//                    Log.e("MedicineCartFragment", "Failed to add item to medicineOrders", e)
//                }
//        }
//    }
    private fun getCurrentDate(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }

    private fun getCurrentTime(): String {
        val formatter = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
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
