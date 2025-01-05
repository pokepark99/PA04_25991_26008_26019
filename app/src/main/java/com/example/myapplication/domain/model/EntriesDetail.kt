package com.example.myapplication.domain.model

data class EntriesDetail(
    val id: String,
    val userId: String,
    val userName: String,
    val state: Int,
    val positionsId: String,
    val positionName: String,
    val schedulesId: String,
    val scheduleDate: String
)