package com.example.myapplication.domain.model

import java.sql.Time
import java.util.Date

data class Schedules(
    val id: Int,
    val storesId: Int,
    val date: Date,
    val timeStart: Time,
    val timeEnd: Time
)