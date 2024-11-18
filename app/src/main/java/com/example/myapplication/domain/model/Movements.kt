package com.example.myapplication.domain.model

data class Movements(
    val id: Int,
    val itemsId: Int,
    val type: Boolean,
    val quantity: Int,
    val visitsId: Int,
    val donationsId: Int,
    val requestsId: Int,
    val notes: String
)