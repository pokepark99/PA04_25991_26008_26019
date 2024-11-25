package com.example.myapplication.domain.model

data class Stores(
    val id: Int,
    val name: String,
    val location: String
) {
    companion object {
        fun addStore(
            list: MutableList<Stores>,
            id: Int,
            name: String,
            location: String
        ) {
            val store = Stores(id, name, location)
            list.add(store)
        }
    }
}