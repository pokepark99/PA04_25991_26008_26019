package com.example.myapplication.domain.model

data class Movements(
    val id: String,
    val itemsId: String,
    val type: Boolean,
    val quantity: Int,
    val visitsId: String,
    val donationsId: String,
    val requestsId: String,
    val notes: String,
    val donationsEntitiesId: String
) {
    companion object {
        fun addMovement(
            list: MutableList<Movements>,
            id: String,
            itemsId: String,
            type: Boolean,
            quantity: Int,
            visitsId: String,
            donationsId: String,
            requestsId: String,
            notes: String,
            donationsEntitiesId: String
        ) {
            val movement = Movements(id, itemsId, type, quantity, visitsId, donationsId, requestsId, notes,
                donationsEntitiesId)
            list.add(movement)
        }
    }
}