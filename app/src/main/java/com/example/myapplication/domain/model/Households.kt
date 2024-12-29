package com.example.myapplication.domain.model

data class Households(
    val id: String,
    val visitors: List<String>,
    val notes: String
) {
    companion object {
        fun addHousehold(
            list: MutableList<Households>,
            id: String,
            visitors: List<String>,
            notes:String
        ) {
            val household = Households(id, visitors, notes)
            list.add(household)
        }
    }
}