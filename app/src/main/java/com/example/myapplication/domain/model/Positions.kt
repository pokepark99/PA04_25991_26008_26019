package com.example.myapplication.domain.model

data class Positions(
    val id: String,
    val name: String,
    val description: String
) {
    companion object {
        fun addPosition(
            list: MutableList<Positions>,
            id: String,
            name: String,
            description: String
        ) {
            val position = Positions(id, name, description)
            list.add(position)
        }
    }
}