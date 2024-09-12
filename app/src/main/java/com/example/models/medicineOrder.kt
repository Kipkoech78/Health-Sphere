package com.example.models

data class medicineOrder(
    val orderId: String = "", // Firestore will automatically generate this
    val userId: String = "",
    val drugName: String = "",
    val time: String = "",
    val date: String = "",
    val fees: String = "",
    val chemist: String = ""

)
