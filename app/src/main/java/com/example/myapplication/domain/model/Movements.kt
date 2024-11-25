package com.example.myapplication.domain.model

data class Movements(
    val id: Int,
    val itemsId: Int,
    val type: Boolean,
    val quantity: Int,
    val visitsId: Int,
    val donationsId: Int,
    val requestsId: Int,
    val notes: String,
    val donationsEntitiesId: Int
) {
    companion object {
        fun addMovement(
            list: MutableList<Movements>,
            id: Int,
            itemsId: Int,
            type: Boolean,
            quantity: Int,
            visitsId: Int,
            donationsId: Int,
            requestsId: Int,
            notes: String,
            donationsEntitiesId: Int
        ) {
            val movement = Movements(id, itemsId, type, quantity, visitsId, donationsId, requestsId, notes,
                donationsEntitiesId)
            list.add(movement)
        }
    }
}