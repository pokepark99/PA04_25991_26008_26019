package com.example.myapplication.domain.model

import java.util.Date

data class Users(
    val id: Int,
    val name: String,
    val dob: Date,
    val taxNo: Int,
    val countriesId: Int,
    val admin: Boolean,
    val email:String,
    val password: String,
    val state: Boolean,
    val photo: String
)