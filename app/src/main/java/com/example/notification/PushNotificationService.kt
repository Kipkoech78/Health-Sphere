package com.example.notification

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.FirebaseMessaging

class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "New token: $token")
        // Store the token in Firestore, Realtime Database, or your backend
        saveTokenToDatabase(token)
    }

    private fun saveTokenToDatabase(token: String) {
        // Example: Save the token in Firestore
        val db = FirebaseFirestore.getInstance()
        val userId = "some_unique_user_id"  // Get this from your user management system

        val tokenMap = hashMapOf(
            "token" to token
        )

        db.collection("users")
            .document(userId)
            .set(tokenMap)
            .addOnSuccessListener {
                Log.d("FCM Token", "Token successfully saved")
            }
            .addOnFailureListener {
                Log.d("FCM Token", "Failed to save token")
            }
    }
}
