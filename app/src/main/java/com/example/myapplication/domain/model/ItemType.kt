package com.example.myapplication.domain.model

data class ItemType(
    val id: String,
    val description: String
) {
    companion object {
        fun addItemType(
            list: MutableList<ItemType>,
            id: String,
            description: String
        ) {
            val itemType = ItemType(id, description)
            list.add(itemType)
        }
    }
}