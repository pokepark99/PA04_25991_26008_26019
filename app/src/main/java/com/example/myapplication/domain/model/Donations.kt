package com.example.myapplication.domain.model

import java.util.Date

data class Donations(
    val id: Int,
    val date: Date,
    val notes:String,
    val donorName: String,
    val donorPhoneNo : Int,
    val donorEmail: String,
    val entitiesId: Int
) {
    companion object { //pode ser chamado pelo nome da class. Ex. Donations.addDonation()
        fun addDonation(
            list: MutableList<Donations>, //recebe a lista de donacoes ja existente
            id: Int,
            date:Date,
            notes: String,
            donorName: String,
            donorPhoneNo: Int,
            donorEmail: String,
            entitiesId: Int
        ) {
            val donation = Donations(id, date, notes, donorName, donorPhoneNo, donorEmail, entitiesId)
            list.add(donation) //adiciona a noca donacao a lista de donacoes
        }
    }
}