package com.example.myapplication.domain.model

import java.util.Date

data class Entries(
    val id: Int,
    val schedulesId: Int,
    val positionsId: Int,
    val usersId: Int,
    val state: Boolean
) {
    companion object {
        fun addEntry(
            list: MutableList<Entries>,
            id: Int,
            schedulesId: Int,
            positionsId: Int,
            usersId: Int,
            state: Boolean
        ) {
            val entry = Entries(id, schedulesId, positionsId, usersId, state)
            list.add(entry)
        }
    }
}