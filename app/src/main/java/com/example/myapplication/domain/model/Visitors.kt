package com.example.myapplication.domain.model

import com.google.firebase.Timestamp

data class Visitors (
    var id: String = "",
    val name: String = "",
    val dob: Timestamp = Timestamp.now(),
    val taxNo: Int = 0,
    val countriesId: String = ""
) {
    companion object {
        fun addVisitor(
            list: MutableList<Visitors>,
            id: String,
            name: String,
            dob: Timestamp,
            taxNo: Int,
            countriesId: String
        ) {
            val visitor = Visitors(id, name, dob, taxNo,countriesId)
            list.add(visitor)
        }
    }
}