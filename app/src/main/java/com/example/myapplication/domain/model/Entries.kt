package com.example.myapplication.domain.model

import java.util.Date

data class Entries(
    val id: String,
    val schedulesId: String,
    val positionsId: String,
    val usersId: String,
    val state: Boolean
) {
    companion object {
        fun addEntry(
            list: MutableList<Entries>,
            id: String,
            schedulesId: String,
            positionsId: String,
            usersId: String,
            state: Boolean
        ) {
            val entry = Entries(id, schedulesId, positionsId, usersId, state)
            list.add(entry)
        }
    }
}