package com.example.myapplication.domain.model

import java.util.Date

data class Requests(
    val id: Int,
    val entitiesId: Int,
    val visitorsId: Int,
    val description: String,
    val notes: String,
    val quantity: Int,
    val dateStart: Date,
    val dateEnd: Date
)