package com.example.healthsphere

import android.app.AlertDialog
import android.app.Dialog
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

open class BazeActivity : AppCompatActivity() {
    private lateinit var mprogressDialog: Dialog
    // Show dynamic payment options
    fun showPaymentOptionsDialog(
        totalPrice: Double,
        accountBalance: Long,
        availableOptions: Array<String>,
        onPaymentComplete: (Boolean) -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle("Payment Options")
            .setItems(availableOptions) { _, which ->
                Log.d("BazeActivity", "Selected option: ${availableOptions[which]}")
                when (availableOptions[which]) {
                    "Account" -> handleAccountPayment(totalPrice, accountBalance, onPaymentComplete)
                    "Mpesa" -> handleMpesaPayment(totalPrice)
                    "Cancel" -> {
                        Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show()
                        onPaymentComplete(false)
                    }
                }
            }
            .show()
    }

//    fun showPaymentOptionsDialog(
//        totalPrice: Double,
//        accountBalance: Long,
//        availableOptions: Array<String>, // Dynamic payment options
//        onPaymentComplete: (Boolean) -> Unit
//    ) {
//        AlertDialog.Builder(this)
//            .setTitle("Payment Options")
//            .setItems(availableOptions) { _, which ->
//                when (availableOptions[which]) {
//                    "Account" -> handleAccountPayment(totalPrice, accountBalance, onPaymentComplete)
//                    "Mpesa" -> handleMpesaPayment(totalPrice)
//                    "Cancel" -> {
//                        Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show()
//                        onPaymentComplete(false)
//                    }
//                }
//            }
//            .show()
//    }

    // Handle account payment
    private fun handleAccountPayment(
        totalPrice: Double,
        accountBalance: Long,
        onPaymentComplete: (Boolean) -> Unit
    ) {
        if (accountBalance >= totalPrice) {
            Log.d("BazeActivity", "Sufficient balance. Proceeding with payment")
            deductFromAccount(totalPrice) { success ->
                if (success) {
                    Log.d("BazeActivity", "Account payment successful")
                    onPaymentComplete(true)
                } else {
                    Log.e("BazeActivity", "Account payment failed")
                    onPaymentComplete(false)
                }
            }
        } else {
            Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show()
            Log.d("BazeActivity", "Insufficient balance for payment")
            onPaymentComplete(false)
        }
    }


    // Deduct from account
    open fun deductFromAccount(totalPrice: Double, onDeductionComplete: (Boolean) -> Unit) {
        // Firebase or other logic to deduct from account
        Toast.makeText(this, "Deducting from account...", Toast.LENGTH_SHORT).show()
        onDeductionComplete(true) // Simulating a successful deduction
    }

    // Handle Mpesa payment
    open fun handleMpesaPayment(totalPrice: Double) {
        Toast.makeText(this, "Simulating Mpesa payment of Ksh $totalPrice", Toast.LENGTH_SHORT).show()
        // Assuming success for now
        Log.d("BazeActivity", "Mpesa payment simulated as successful")
        // You might call onPaymentComplete(true) here if you expect immediate success.
    }

    //payments End
    fun showErrorSnackBar(message: String, errorMessage: Boolean){
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackbarView = snackBar.view

        if(errorMessage){
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(this, R.color.error_message)
            )
        }
        else{
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(this,
                R.color.sucess_message
            ))
        }
        snackBar.show()
    }

    fun showProgressDialog(text: String){
        mprogressDialog = Dialog(this)
        //set the screen context from a layout resource the resource will be inflated adding all top level views to the screen
        mprogressDialog.setContentView(R.layout.dialog_progress)
//        tv_progress_text = findViewById(R.id.tv_progress_text)
        // Access the TextView inside the dialog and set its text
        val tv_progress_text = mprogressDialog.findViewById<TextView>(R.id.tv_progress_text)
        tv_progress_text.text = text

//        mprogressDialog.tv_progress_text = text = text
        mprogressDialog.setCancelable(true)
        mprogressDialog.setCanceledOnTouchOutside(false)
        mprogressDialog.show()
    }
    fun hideProgressDialog(){
        mprogressDialog.dismiss()
    }


}