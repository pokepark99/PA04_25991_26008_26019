package com.example.myapplication.domain.model

import java.util.Date

data class Infractions (
    val visitorsId: Int,
    val infractionTypesId:Int,
    val date: Date,
    val notes: String
) {
    companion object {
        fun addInfraction(
            list: MutableList<Infractions>,
            visitorsId: Int,
            infractionTypesId: Int,
            date: Date,
            notes: String
        ) {
            val infraction = Infractions(visitorsId, infractionTypesId, date, notes)
            list.add(infraction)
        }
    }
}