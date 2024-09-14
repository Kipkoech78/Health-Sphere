package com.example.accounts

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.Adapters.CombinedAdapter
import com.example.Adapters.OrdersAdapter
import com.example.firestore.FirestoreClass
import com.example.healthsphere.R
import com.example.utils.Constants

class UserAccountActivity : AppCompatActivity() {
    private lateinit var balanceTextView: TextView
    private lateinit var depositButton: Button
    private lateinit var tvTittle: TextView
    private lateinit var depositAmountEditText: EditText
    private lateinit var ordersListView: ListView
    private val firestoreClass = FirestoreClass()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_account_activity)
        // Initialize views
        balanceTextView = findViewById(R.id.balanceTextView)
        depositButton = findViewById(R.id.depositButton)
        depositAmountEditText = findViewById(R.id.depositAmountEditText)
        ordersListView = findViewById(R.id.ordersListView)
        tvTittle = findViewById(R.id.tvTittle)

        // Get userId from SharedPreferences
        val sharedPreferences =
            getSharedPreferences(Constants.HEALTHAPP_PREFERENCES, Context.MODE_PRIVATE)
        userId = sharedPreferences.getString(Constants.LOGGED_IN_USER_ID, "")

        if (userId.isNullOrEmpty()) {
            Log.e("UserAccountActivity", "Error: userId is null or empty")
            return
        }

        // Load user balance and orders
        loadUserBalance()
        loadUserOrders()

        depositButton.setOnClickListener {
            val depositAmount = depositAmountEditText.text.toString().toFloatOrNull() ?: 0f
            if (depositAmount > 0) {
                firestoreClass.depositToUserAccount(userId!!, depositAmount,
                    onSuccess = {
                        Log.d("UserAccountActivity", "Deposit successful")
                        loadUserBalance() // Refresh balance
                    },
                    onFailure = { e ->
                        Log.e("UserAccountActivity", "Error depositing money", e)
                    }
                )
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadUserBalance() {
        userId?.let { id ->
            firestoreClass.getUserDetailsAcc(id,
                onSuccess = { user ->
                    balanceTextView.text = "Balance: Ksh ${user.balance}"
                },
                onFailure = { e ->
                    Log.e("UserAccountActivity", "Error loading user balance", e)
                }
            )
        }
    }
    private fun loadUserOrders() {
        userId?.let { id ->
            firestoreClass.getUserOrders(id,
                onSuccess = { orders ->
                    val adapter = OrdersAdapter(this, orders)
                    ordersListView.adapter = adapter
                },
                onFailure = { e ->
                    Log.e("UserAccountActivity", "Error loading user orders", e)
                }
            )
        }
    }
}

