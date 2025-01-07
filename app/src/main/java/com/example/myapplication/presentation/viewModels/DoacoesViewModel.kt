package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Donations
import com.example.myapplication.domain.model.Entities
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Locale

class DoacoesViewModel : ViewModel(){
    private val firestore = Firebase.firestore

    private val _entities = MutableStateFlow<List<Entities>>(emptyList())
    val entities: StateFlow<List<Entities>> = _entities

    init {
        fetchEntities()
    }
    //busca as entidades ao firestore
    private fun fetchEntities() {
        firestore.collection("Entities").addSnapshotListener{ snapshot, _ ->
            val entidades = snapshot?.documents?.map {
                Entities(
                    id = it.id,
                    name = it.getString("Name") ?: "Unknown",
                    phoneNo = it.getLong("PhoneNo")?.toInt() ?: 0,
                    email = it.getString("Email") ?: "",
                    address = it.getString("Address") ?: "",
                    notes = it.getString("Notes") ?: ""
                )
            } ?: emptyList()
            _entities.value = entidades
        }
    }
    // adiciona nova doação ao firestorage
    fun addDonation(entidadeId:String?, donorName:String, donorEmail:String, donorPhone:Int?, notes:String, date:String){

        // muda a data para formato com.google.Timestamp
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateFB = try {
            com.google.firebase.Timestamp(dateFormat.parse(date)!!)
        } catch (e: Exception) {
            Log.e("DoacoesViewModel", "Invalid date format", e)
            return
        }

        val donationData = mapOf(
            "Date" to dateFB,
            "DonorEmail" to donorEmail,
            "DonorName" to donorName,
            "DonorPhoneNo" to donorPhone,
            "EntitiesId" to entidadeId,
            "Notes" to notes
        )

        firestore.collection("Donations")
            .add(donationData)
            .addOnSuccessListener {
                Log.d("DoacoesViewModel", "Donation successfully added with ID: ${it.id}")
            }
            .addOnFailureListener { e ->
                Log.e("DoacoesViewModel", "Error adding donation", e)
            }
    }
    //busca todas as doações ao firestore, atualizando se houver mudanças
    fun fetchDonations(callback: (List<Donations>) -> Unit){
        firestore.collection("Donations")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("DoacoesViewModel", "Error fetching donations: ", exception)
                    callback(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val donationsList = snapshot.documents.mapNotNull { document ->
                        val id = document.id
                        val date = document.getTimestamp("Date")
                        val entitiesId = document.getString("EntitiesId") ?: ""
                        val donorName = document.getString("DonorName") ?: ""
                        val donorEmail = document.getString("DonorEmail") ?: ""
                        val donorPhone = document.getLong("DonorPhoneNo")?.toInt()
                        val notes = document.getString("Notes") ?: ""

                        if (date != null) {
                            Donations(
                                id = id,
                                entitiesId = entitiesId,
                                donorName = donorName,
                                donorEmail = donorEmail,
                                donorPhoneNo = donorPhone,
                                notes = notes,
                                date = date
                            )
                        } else {
                            null
                        }
                    }
                    callback(donationsList)
                } else {
                    callback(emptyList())
                }
            }
    }
    // apaga uma doação
    fun deleteDonation(donationId:String){
        val donationDel = FirebaseFirestore.getInstance().collection("Donations").document(donationId)
        Log.d("DoacoesViewModel", "Attempting to delete document: ${donationDel.path}")

        donationDel.delete()
            .addOnSuccessListener {
                Log.d("DoacoesViewModel", "Donation deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("DoacoesViewModel", "Error deleting donation", exception)
            }
    }
    //atualiza os dados de uma doação
    fun updateDonation(
        id: String,
        notes: String,
        date: String,
        donorName: String?,
        donorEmail: String?,
        donorPhoneNo: Int?

    ){
        val updates = mutableMapOf<String, Any>(
            "Notes" to notes,
            "Date" to date
        )
        // atualiza os dados se nao forem null
        date.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date_ = try {
                com.google.firebase.Timestamp(dateFormat.parse(it)!!)
            } catch (e: Exception) {
                Log.e("DoacoesViewModel", "Invalid date format", e)
                return
            }
            updates["Date"] = date_
        }
        donorName?.let { updates["DonorName"] = it }
        donorEmail?.let { updates["DonorEmail"] = it }
        donorPhoneNo?.let { updates["DonorPhoneNo"] = it }

        firestore.collection("Donations").document(id).update(updates)
            .addOnSuccessListener {
                Log.d("DoacoesViewModel", "Donation updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("DoacoesViewModel", "Error updating donation", e)
            }
    }
}