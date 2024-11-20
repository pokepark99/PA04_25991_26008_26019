package com.example.myapplication.domain.model

import java.util.Date

data class Donations(
    val id: Int,
    val date: Date,
    val notes:String,
    val donorName: String,
    val donorPhoneNo : Int,
    val donorEmail: String,
    val entitiesId: Int
)