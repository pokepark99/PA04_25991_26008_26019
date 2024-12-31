package com.example.myapplication.domain.model

import com.google.firebase.Timestamp

data class Schedules(
    val id: String,
    val storeId: String,
    val dateStart: Timestamp,
    val dateEnd: Timestamp,
    val open: Boolean
) {
    companion object {
        fun addSchedule(
            list: MutableList<Schedules>,
            id: String,
            storeId: String,
            dateStart: Timestamp,
            dateEnd: Timestamp,
            open: Boolean
        ) {
            val schedule = Schedules(id, storeId, dateStart, dateEnd, open)
            list.add(schedule)
        }
    }
}