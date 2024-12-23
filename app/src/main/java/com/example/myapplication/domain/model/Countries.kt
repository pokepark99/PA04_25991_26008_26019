package com.example.myapplication.domain.model

data class Countries(
    val id: String,
    val name: String
) {
    companion object { //pode ser chamado pelo nome da class. Ex. Countries.addCountry()
        fun addCountry(
            list: MutableList<Countries>, //recebe a lista de paises ja existente
            id: String,
            name: String
        ) {
            val country = Countries(id, name)
            list.add(country) //adiciona o novo pais a lista de paises
        }
    }
}