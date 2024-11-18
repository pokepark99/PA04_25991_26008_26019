package com.example.myapplication.domain.model

import java.util.Date

data class Infractions (
    val visitorsId: Int,
    val infractionTypesId:Int,
    val date: Date,
    val notes: String
)