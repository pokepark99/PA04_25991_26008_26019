package com.example.myapplication.domain.model

import java.util.Date

data class Visits (
    val id: Int,
    val visitorsId: Int,
    val date: Date,
    val storesId: Int
)