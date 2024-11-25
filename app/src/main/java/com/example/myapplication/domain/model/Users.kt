package com.example.myapplication.domain.model

import java.util.Date

data class Users(
    val id: Int,
    val name: String,
    val dob: Date,
    val taxNo: Int,
    val countriesId: Int,
    val admin: Boolean,
    val email:String,
    val password: String,
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
            email: String,
            password: String,
            state: Boolean,
            photo: String,
            city: String,
            phoneNo: Int
        ) {
            val user = Users(id, name, dob, taxNo,countriesId, admin, email, password, state, photo, city,
                phoneNo)
            list.add(user)
        }
    }
}