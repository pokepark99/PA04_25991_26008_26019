package com.example.myapplication.domain.model

data class Entities(
    val id: Int,
    val name: String,
    val phoneNo: Int,
    val email: String,
    val address: String,
    val notes: String
) {
    companion object { //pode ser chamado pelo nome da class. Ex. Entities.addEntity()
        fun addEntity(
            list: MutableList<Entities>, //recebe a lista de entidades ja existente
            id: Int,
            name: String,
            phoneNo: Int,
            email: String,
            address: String,
            notes: String
        ) {
            val entity = Entities(id, name, phoneNo, email, address, notes)
            list.add(entity) //adiciona a nova entidade a lista de entidades
        }
    }
}