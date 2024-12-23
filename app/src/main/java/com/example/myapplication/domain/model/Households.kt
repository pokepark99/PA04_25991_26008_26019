package com.example.myapplication.domain.model

data class Households(
    val id: String,
    val name: String,
    val notes: String
) {
    companion object {
        fun addHousehold(
            list: MutableList<Households>,
            id: String,
            name: String,
            notes:String
        ) {
            val household = Households(id, name, notes)
            list.add(household)
        }
    }
}