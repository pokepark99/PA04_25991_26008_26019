package com.example.myapplication.domain.model

import java.util.Date

data class Visits (
    val id: Int,
    val visitorsId: Int,
    val date: Date,
    val storesId: Int
) {
    companion object {
        fun addVisit(
            list: MutableList<Visits>,
            id: Int,
            visitorsId: Int,
            date: Date,
            storesId: Int
        ) {
            val visit = Visits(id, visitorsId, date, storesId)
            list.add(visit)
        }
    }
}