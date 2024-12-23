package com.example.myapplication.domain.model

data class Requests(
    val id: String,
    val entitiesId: String,
    val visitorsId: String,
    val description: String,
    val notes: String,
    val quantity: Int,
    val dateStart: com.google.firebase.Timestamp,
    val dateEnd: com.google.firebase.Timestamp
) {
    companion object {
        fun addRequest(
            list: MutableList<Requests>,
            id: String,
            entitiesId: String,
            visitorsId: String,
            description: String,
            notes: String,
            quantity: Int,
            dateStart: com.google.firebase.Timestamp,
            dateEnd: com.google.firebase.Timestamp
        ) {
            val request = Requests(id, entitiesId, visitorsId, description, notes, quantity, dateStart, dateEnd)
            list.add(request)
        }
    }
}