package com.example.myapplication.domain.model

data class Households(
    val id: Int,
    val name: String,
    val notes: String
) {
    companion object {
        fun addHousehold(
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