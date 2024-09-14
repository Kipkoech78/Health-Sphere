package com.example.models
data class Appointment(
    var id: String = "",
    val userId: String = "",
    val address: String = "",
    val completed: Boolean = false,
    val date: String = "",
    val email: String = "", // Doctor's email
    val fees: String = "",
    val name: String = "",
    val time: String = "",
    var status: String = "Active",
    val username: String= "",
    val userEmail: String = "" // User's email who made the appointment
)

