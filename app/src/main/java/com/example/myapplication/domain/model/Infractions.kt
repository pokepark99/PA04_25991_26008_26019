package com.example.myapplication.domain.model

import com.google.firebase.Timestamp

data class Infractions (
    val visitorsId: String,
    val date: Timestamp,
    val severity : Int
) {
    companion object {
        fun addInfraction(
            list: MutableList<Infractions>,
            visitorsId: String,
            date: Timestamp,
            severity: Int
        ) {
            val infraction = Infractions(visitorsId, date, severity)
            list.add(infraction)
        }
    }
}