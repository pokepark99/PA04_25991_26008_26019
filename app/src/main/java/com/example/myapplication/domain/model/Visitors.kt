package com.example.myapplication.domain.model

import java.util.Date

data class Visitors (
    val id: Int,
    val name: String,
    val dob: Date,
    val taxNo: Int,
    val countriesId: Int
)