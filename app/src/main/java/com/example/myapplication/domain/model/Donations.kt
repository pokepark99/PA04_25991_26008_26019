package com.example.myapplication.domain.model

data class Donations(
    val id: String,
    val date: com.google.firebase.Timestamp,
    val notes:String,
    val donorName: String,
    val donorPhoneNo : Int?,
    val donorEmail: String,
    val entitiesId: String
) {
    companion object { //pode ser chamado pelo nome da class. Ex. Donations.addDonation()
        fun addDonation(
            list: MutableList<Donations>, //recebe a lista de donacoes ja existente
            id: String,
            date:com.google.firebase.Timestamp,
            notes: String,
            donorName: String,
            donorPhoneNo: Int?,
            donorEmail: String,
            entitiesId: String
        ) {
            val donation = Donations(id, date, notes, donorName, donorPhoneNo, donorEmail, entitiesId)
            list.add(donation) //adiciona a noca donacao a lista de donacoes
        }
    }
}