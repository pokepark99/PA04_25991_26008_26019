package com.example.myapplication.domain.model

data class Visitors (
    val id: String,
    val name: String,
    val dob: com.google.firebase.Timestamp,
    val taxNo: Int,
    val countriesId: String
) {
    companion object {
        fun addVisitor(
            list: MutableList<Visitors>,
            id: String,
            name: String,
            dob: com.google.firebase.Timestamp,
            taxNo: Int,
            countriesId: String
        ) {
            val visitor = Visitors(id, name, dob, taxNo,countriesId)
            list.add(visitor)
        }
    }
}