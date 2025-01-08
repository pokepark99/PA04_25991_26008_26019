package com.example.myapplication.domain.model

import com.google.firebase.Timestamp

data class Visitors (
    var id: String = "",
    val name: String = "",
    val dob: Timestamp = Timestamp.now(),
    val phoneNo: Int = 0,
    val countriesId: String = "",
    val nif: Int = 0
) {
    companion object {
        fun addVisitor(
            list: MutableList<Visitors>,
            id: String,
            name: String,
            dob: Timestamp,
            phoneNo: Int,
            countriesId: String,
            nif: Int
        ) {
            val visitor = Visitors(id, name, dob, phoneNo,countriesId, nif)
            list.add(visitor)
        }
    }
}