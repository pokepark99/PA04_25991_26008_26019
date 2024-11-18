package com.example.myapplication.domain.model

data class Entries(
    val id: Int,
    val schedulesId: Int,
    val positionsId: Int,
    val usersId: Int,
    val state: Boolean
)