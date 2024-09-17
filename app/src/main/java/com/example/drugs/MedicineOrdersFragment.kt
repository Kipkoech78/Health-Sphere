package com.example.drugs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.Adapters.MedicineOrdersAdapter
import com.example.healthsphere.R
import com.example.models.medicineOrder
import com.example.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedicineOrdersFragment : Fragment() {

    private lateinit var listViewOrders: ListView
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var ordersAdapter: MedicineOrdersAdapter
    private lateinit var orders: List<medicineOrder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.medicine_orders_fragment, container, false)

        listViewOrders = view.findViewById(R.id.listViewOrders)
        // Load orders
        loadOrders()
        return view
    }

    private fun loadOrders() {
        val sharedPreferences = requireContext().getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString(Constants.LOGGED_IN_USER_ID, "")!!
        if (userId != null){
            Log.d("MedicineOrderFragment", "Fetching Orsers items for user: $userId")
            db.collection("medicines_orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    orders = documents.toObjects(medicineOrder::class.java)

                    // Set up the adapter
                    ordersAdapter = MedicineOrdersAdapter(requireContext(), orders)
                    listViewOrders.adapter = ordersAdapter

                    Log.d("MedicineOrdersFragment", "Orders fetched successfully: ${orders.size}")
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Failed to load orders", Toast.LENGTH_SHORT).show()
                }

        } else{
            Log.d("MedicineOrderFragment", "No userId found in SharedPreferences")
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()

        }
    }
}
