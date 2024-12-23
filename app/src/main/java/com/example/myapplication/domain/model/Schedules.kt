package com.example.myapplication.domain.model

data class Schedules(
    val id: String,
    val storesId: String,
    val dateStart: com.google.firebase.Timestamp,
    val dateEnd: com.google.firebase.Timestamp
) 