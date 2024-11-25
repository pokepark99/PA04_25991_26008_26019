package com.example.myapplication.domain.model

import java.util.Date

data class Visitors (
    val id: Int,
    val name: String,
    val dob: Date,
    val taxNo: Int,
    val countriesId: Int
) {
    companion object {
        fun addVisitor(
            list: MutableList<Visitors>,
            id: Int,
            name: String,
            dob: Date,
            taxNo: Int,
            countriesId: Int
        ) {
            val visitor = Visitors(id, name, dob, taxNo,countriesId)
            list.add(visitor)
        }
    }
}