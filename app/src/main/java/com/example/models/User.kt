package com.example.models

class User (
    val id: String = "",
    val userName: String = "",
    val mobile: String = "",
    val email: String = "",
    val image: String = "",
    val gender: String = "",
    var balance: Float = 1000f,
    var orders: List<Order> = emptyList(),
    val profileCompleted: Int = 1,
    val role: String = "user"
)



