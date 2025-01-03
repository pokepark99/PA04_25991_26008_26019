package com.example.myapplication.domain.model

data class Users(
    var id: String = "",
    val name: String = "",
    val dob: String = "",
    val countriesId: String = "",
    val admin: Boolean = false,
    val state: Int = 0,
    val photo: String = "",
    val nif: Long = 0,
    val city: String = "",
    val phoneNo: Int = 0
) {
    companion object {
        fun addUser(
            list: MutableList<Users>,
            id: String,
            name: String,
            dob: String,
            countriesId: String,
            admin: Boolean,
            state: Int,
            photo: String,
            nif: Long,
            city: String,
            phoneNo: Int
        ) {
            val user = Users(id, name, dob,countriesId, admin, state, photo, nif, city, phoneNo)
            list.add(user)
        }
    }
}