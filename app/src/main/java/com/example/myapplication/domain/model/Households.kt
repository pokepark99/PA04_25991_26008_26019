package com.example.myapplication.domain.model

data class Households(
    val id: String,
    val visitors: List<String>,
    val name: String
) {
    companion object {
        fun addHousehold(
            list: MutableList<Households>,
            id: String,
            visitors: List<String>,
            name:String
        ) {
            val household = Households(id, visitors, name)
            list.add(household)
        }
    }
}