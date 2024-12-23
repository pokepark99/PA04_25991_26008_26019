package com.example.myapplication.domain.model

data class Visits (
    val id: String,
    val visitorsId: String,
    val date: com.google.firebase.Timestamp,
    val storesId: String
) {
    companion object {
        fun addVisit(
            list: MutableList<Visits>,
            id: String,
            visitorsId: String,
            date: com.google.firebase.Timestamp,
            storesId: String
        ) {
            val visit = Visits(id, visitorsId, date, storesId)
            list.add(visit)
        }
    }
}