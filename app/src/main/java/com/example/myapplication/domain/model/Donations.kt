package com.example.myapplication.domain.model

import java.util.Date

data class Donations(
    val id: Int,
    val entitiesId: Int,
    val date: Date,
    val notes:String
)