package com.example.models

data class CartItem(
    val username: String = "",
    val product: String = "",
    val price: Float = 0.0f,
    val otype: String = "",
    val quantity: Int = 1,
    val time: String = "",
    val date: String = ""
)
