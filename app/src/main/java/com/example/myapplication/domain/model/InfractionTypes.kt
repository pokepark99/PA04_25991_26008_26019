package com.example.myapplication.domain.model

data class InfractionTypes (
    val id:Int,
    val name: String,
    val description: String,
    val severity: Int
) {
    companion object {
        fun addInfractionType(
            list: MutableList<InfractionTypes>,
            id: Int,
            name: String,
            description: String,
            severity: Int
        ) {
            val infractionType = InfractionTypes(id, name, description, severity)
            list.add(infractionType)
        }
    }
}