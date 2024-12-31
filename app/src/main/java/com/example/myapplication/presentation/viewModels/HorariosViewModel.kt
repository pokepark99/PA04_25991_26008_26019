package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Positions
import com.example.myapplication.domain.model.Schedules
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HorariosViewModel : ViewModel() {
    private val firestore = Firebase.firestore

    // buscar lojas ao firestore
    fun fetchStores(callback: (List<Pair<String, String>>) -> Unit) {
        firestore.collection("Stores")
            .get()
            .addOnSuccessListener { documents ->
                val stores = documents.map { it.id to it.getString("Name").orEmpty() }
                callback(stores)
            }
            .addOnFailureListener { e ->
                Log.e("HorariosViewModel", "Error fetching stores", e)
            }
    }
    // adicionar novo horario
    fun addSchedule(dateStart: Timestamp, dateEnd: Timestamp, storeId: String, open: Boolean) {
        val schedule = mapOf(
            "DateStart" to dateStart,
            "DateEnd" to dateEnd,
            "StoresId" to storeId,
            "Open" to open
        )
        firestore.collection("Schedules")
            .add(schedule)
            .addOnSuccessListener {
                Log.d("HorariosViewModel", "Schedule added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("HorariosViewModel", "Error adding schedule", e)
            }
    }
    // buscar horarios ao firestore
    fun fetchSchedules(callback: (List<Schedules>) -> Unit) {
        firestore.collection("Schedules")
            .addSnapshotListener { documents, e ->
                if (e != null) {
                    Log.e("HorariosViewModel", "Error fetching schedules", e)
                    return@addSnapshotListener
                }
                if (documents != null) {
                    val schedules = documents.mapNotNull { document ->
                        val dateStart = document.getTimestamp("DateStart")
                        val dateEnd = document.getTimestamp("DateEnd")
                        val storeId = document.getString("StoresId")
                        val open = document.getBoolean("Open") ?: false

                        Schedules(
                            id = document.id,
                            storeId = storeId ?: "Desconhecido",
                            dateStart = dateStart ?: Timestamp.now(),
                            dateEnd = dateEnd ?: Timestamp.now(),
                            open = open
                        )
                    }
                    callback(schedules)
                }
            }
    }
    //apagar um horario
    fun deleteSchedule(scheduleId:String){
        val scheduleDel = FirebaseFirestore.getInstance().collection("Schedules").document(scheduleId)
        Log.d("HorariosViewModel", "Attempting to delete document: ${scheduleDel.path}")

        scheduleDel.delete()
            .addOnSuccessListener {
                Log.d("HorariosViewModel", "Schedule deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("HorariosViewModel", "Error deleting schedule", exception)
            }
    }
    // abrir/fechar um horario
    fun toggleScheduleStatus(scheduleId: String, isOpen: Boolean){
        val scheduleEdit = firestore.collection("Schedules").document(scheduleId)

        scheduleEdit.update("Open", isOpen)
            .addOnSuccessListener {
                Log.d("HorariosViewModel", "Schedule status updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("HorariosViewModel", "Error updating schedule status", e)
            }
    }
    //obtem os cargos possiveis
    fun fetchPositions(callback: (List<Positions>) -> Unit){
        firestore.collection("Positions")
            .get()
            .addOnSuccessListener { documents ->
                val positions = documents.mapNotNull { document ->
                    val id = document.id
                    val name = document.getString("Name")
                    val description = document.getString("Description")

                    if (name != null && description != null) {
                        Positions(id, name, description)
                    } else {null}
                }
                callback(positions)
            }
            .addOnFailureListener { e ->
                Log.e("HorariosViewModel", "Error fetching positions", e)
            }
    }
    //numero de candidaturas para o horario
    fun getEntriesCountForSchedule(scheduleId: String, countFetched:(Int)->Unit){
        firestore.collection("Entries")
            .whereEqualTo("SchedulesId", scheduleId)
            .get()
            .addOnSuccessListener { snapshot ->
                countFetched(snapshot.size())
            }
            .addOnFailureListener { e ->
                Log.e("HorariosViewModel", "Error fetching entries count", e)
            }
    }
    //adicionar uma nova candidatura a um cargo num horario
    fun addEntry(positionId: String, scheduleId: String){
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val entry = mapOf(
            "PositionsId" to positionId,
            "SchedulesId" to scheduleId,
            "State" to 0,
            "UsersId" to currentUserId
        )
        firestore.collection("Entries")
            .add(entry)
            .addOnSuccessListener {
                Log.d("HorariosViewModel", "Entry added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("HorariosViewModel", "Error adding entry", e)
            }
    }
}