package com.example.myapplication.domain.model

import com.google.firebase.firestore.PropertyName

data class Functionalities(
    var Id: String = "",
    var Name: String = "",
    var Description: String = "",
    var State: Boolean = false
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