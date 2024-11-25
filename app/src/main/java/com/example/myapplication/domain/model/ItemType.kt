package com.example.myapplication.domain.model

data class ItemType(
    val id: Int,
    val description: String
) {
    companion object {
        fun addItemType(
            list: MutableList<ItemType>,
            id: Int,
            description: String
        ) {
            val itemType = ItemType(id, description)
            list.add(itemType)
        }
    }
}