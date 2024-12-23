package com.example.myapplication.domain.model

data class Functionalities(
    val id: String,
    val name: String,
    val description: String,
    val state: Int
) {
    companion object {
        fun addFunctionality(
            list: MutableList<Functionalities>,
            id: String,
            name: String,
            description: String,
            state: Int
        ) {
            val functionality = Functionalities(id, name, description, state)
            list.add(functionality)
        }
    }
}