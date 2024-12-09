package com.example.myapplication.domain.model

import java.util.Date

data class Users(
    val id: Int,
    val name: String,
    val dob: Date,
    val taxNo: Int,
    val countriesId: Int,
    val admin: Boolean,
    val state: Boolean,
    val photo: String,
    val city: String,
    val phoneNo: Int
) {
    companion object {
        fun addUser(
            list: MutableList<Users>,
            id: Int,
            name: String,
            dob: Date,
            taxNo: Int,
            countriesId: Int,
            admin: Boolean,
            state: Boolean,
            photo: String,
            city: String,
            phoneNo: Int
        ) {
            val user = Users(id, name, dob, taxNo,countriesId, admin, state, photo, city,
                phoneNo)
            list.add(user)
        }
    }
}