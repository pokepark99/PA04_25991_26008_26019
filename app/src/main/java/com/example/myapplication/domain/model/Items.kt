package com.example.myapplication.domain.model

data class Items(
    val id: String,
    val itemTypeId: String,
    val name: String,
    val stock: Number
) {
    companion object {
        fun addItem(
            list: MutableList<Items>,
            id: String,
            itemTypeId: String,
            name: String,
            stock: Number
        ) {
            val item = Items(id, itemTypeId, name, stock)
            list.add(item)
        }
    }
}