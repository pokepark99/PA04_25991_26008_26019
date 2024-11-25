package com.example.myapplication.domain.model

import java.util.Date

data class Requests(
    val id: Int,
    val entitiesId: Int,
    val visitorsId: Int,
    val description: String,
    val notes: String,
    val quantity: Int,
    val dateStart: Date,
    val dateEnd: Date
) {
    companion object {
        fun addRequest(
            list: MutableList<Requests>,
            id: Int,
            entitiesId: Int,
            visitorsId: Int,
            description: String,
            notes: String,
            quantity: Int,
            dateStart: Date,
            dateEnd: Date
        ) {
            val request = Requests(id, entitiesId, visitorsId, description, notes, quantity, dateStart, dateEnd)
            list.add(request)
        }
    }
}