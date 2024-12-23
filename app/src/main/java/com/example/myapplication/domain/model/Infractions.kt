package com.example.myapplication.domain.model

import java.util.Date

data class Infractions (
    val visitorsId: String,
    val infractionTypesId:String,
    val date: Date,
    val notes: String
) {
    companion object {
        fun addInfraction(
            list: MutableList<Infractions>,
            visitorsId: String,
            infractionTypesId: String,
            date: Date,
            notes: String
        ) {
            val infraction = Infractions(visitorsId, infractionTypesId, date, notes)
            list.add(infraction)
        }
    }
}