package com.example.myapplication.domain.model

import android.content.ClipData.Item

data class Items(
    val id: Int,
    val itemTypeId: Int,
    val name: String,
    val description: String
) {
    companion object {
        fun addItem(
            list: MutableList<Items>,
            id: Int,
            itemTypeId: Int,
            name: String,
            description: String
        ) {
            val item = Items(id, itemTypeId, name, description)
            list.add(item)
        }
    }
}