package com.example.myapplication.domain.model

import android.content.ClipData.Item

data class Items(
    val id: String,
    val itemTypeId: String,
    val name: String,
    val description: String
) {
    companion object {
        fun addItem(
            list: MutableList<Items>,
            id: String,
            itemTypeId: String,
            name: String,
            description: String
        ) {
            val item = Items(id, itemTypeId, name, description)
            list.add(item)
        }
    }
}