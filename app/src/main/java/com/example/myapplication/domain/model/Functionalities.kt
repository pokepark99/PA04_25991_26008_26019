package com.example.myapplication.domain.model

data class Functionalities(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val state: Boolean = false
) {
    companion object {
        fun addFunctionality(
            list: MutableList<Functionalities>,
            id: String,
            name: String,
            description: String,
            state: Boolean
        ) {
            val functionality = Functionalities(id, name, description, state)
            list.add(functionality)
        }
    }
}