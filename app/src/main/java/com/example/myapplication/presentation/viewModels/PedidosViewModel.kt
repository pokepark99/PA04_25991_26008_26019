package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Entities
import com.example.myapplication.domain.model.Requests
import com.example.myapplication.domain.model.Visitors
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Locale

class PedidosViewModel : ViewModel(){
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

    // pesquise dos visitantes
    fun searchVisitors(query: String, callback: (List<Visitors>) -> Unit) {
        val lowerCaseQuery = query.lowercase(Locale.getDefault())

        firestore.collection("Visitors")
            .get()
            .addOnSuccessListener { snapshot ->
                val visitorsList = snapshot.documents.mapNotNull { document ->
                    val id = document.id
                    val name = document.getString("Name")
                    val dob = document.getTimestamp("DOB")
                    val taxNo = document.getLong("TaxNO")?.toInt()
                    val countriesId = document.getString("CountriesId")

                    if (name != null && dob != null && taxNo != null && countriesId != null) {
                        Visitors(id, name, dob, taxNo, countriesId)
                    } else {
                        null
                    }
                }.filter { visitor ->
                    visitor.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                            visitor.taxNo.toString().contains(lowerCaseQuery)
                }
                callback(visitorsList)
            }
            .addOnFailureListener { e ->
                Log.e("VisitasViewModel", "Error fetching visitors: ", e)
                callback(emptyList())
            }
    }

    //adicionar novo pedido ao firestore
    fun addRequest(
        date: String,
        entidadeId: String?,
        visitorId: String?,
        notes: String,
        quantity: Int
    ){
        if ((entidadeId == null && visitorId == null) || (entidadeId != null && visitorId != null)) {
            Log.e("PedidosViewModel", "Invalid input: Select either a visitor or an entity, but not both.")
            return
        }

        // muda a data para formato com.google.Timestamp
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateStart = try {
            com.google.firebase.Timestamp(dateFormat.parse(date)!!)
        } catch (e: Exception) {
            Log.e("PedidosViewModel", "Invalid date format", e)
            return
        }

        val requestData = mapOf(
            "DateStart" to dateStart,
            "DateEnd" to com.google.firebase.Timestamp(0, 0), // Null representation
            "EntitiesId" to entidadeId,
            "VisitorsId" to visitorId,
            "Notes" to notes,
            "Quantity" to quantity
        )

        firestore.collection("Requests")
            .add(requestData)
            .addOnSuccessListener {
                Log.d("PedidosViewModel", "Request successfully added with ID: ${it.id}")
            }
            .addOnFailureListener { e ->
                Log.e("PedidosViewModel", "Error adding request", e)
            }
    }

    // busca dos pedidos ativos (tem DateEnd como 1 de Janeiro 1970)
    fun fetchActiveRequests(callback: (List<Requests>) -> Unit) {
        val dateEnd = com.google.firebase.Timestamp(0, 0) // representa 1 de janeiro 1970

        firestore.collection("Requests")
            .whereEqualTo("DateEnd", dateEnd)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("PedidosViewModel", "Error fetching active requests: ", exception)
                    callback(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val requestsList = snapshot.documents.mapNotNull { document ->
                        val id = document.id
                        val entitiesId = document.getString("EntitiesId")
                        val visitorsId = document.getString("VisitorsId")
                        val description = document.getString("Description") ?: ""
                        val notes = document.getString("Notes") ?: ""
                        val quantity = document.getLong("Quantity")?.toInt() ?: 0
                        val dateStart = document.getTimestamp("DateStart")
                        val dateEnd = document.getTimestamp("DateEnd")

                        if (dateStart != null && dateEnd != null) {
                            Requests(
                                id,
                                entitiesId ?: "",
                                visitorsId ?: "",
                                description,
                                notes,
                                quantity,
                                dateStart,
                                dateEnd
                            )
                        } else {
                            null
                        }
                    }
                    callback(requestsList)
                } else {
                    callback(emptyList())
                }
            }
    }
    //conclui um pedido
    fun concludeRequest(requestId: String) {
        val currentTimestamp = com.google.firebase.Timestamp.now()

        val requestRef = firestore.collection("Requests").document(requestId)
        requestRef.update("DateEnd", currentTimestamp)
            .addOnSuccessListener {
                Log.d("PedidosViewModel", "Request concluded successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("PedidosViewModel", "Error concluding request", exception)
            }
    }
    //apaga um pedido
    fun deleteRequest(requestId : String){
        val requestDel = FirebaseFirestore.getInstance().collection("Requests").document(requestId)
        Log.d("PedidosViewModel", "Attempting to delete document: ${requestDel.path}")

        requestDel.delete()
            .addOnSuccessListener {
                Log.d("PedidosViewModel", "Request deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("PedidosViewModel", "Error deleting request", exception)
            }
    }
    //edita os dados de um pedido
    fun editRequest(requestId: String, date: String?, notes: String?, quantity: Int?) {
        val updates = mutableMapOf<String, Any>()

        // atualiza os dados se nao forem null
        date?.let {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateStart = try {
                com.google.firebase.Timestamp(dateFormat.parse(it)!!)
            } catch (e: Exception) {
                Log.e("PedidosViewModel", "Invalid date format", e)
                return
            }
            updates["DateStart"] = dateStart
        }
        notes?.let { updates["Notes"] = it }
        quantity?.let { updates["Quantity"] = it }

        if (updates.isEmpty()) {
            Log.w("PedidosViewModel", "No fields to update")
            return
        }

        // atualiza os dados
        firestore.collection("Requests")
            .document(requestId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("PedidosViewModel", "Request successfully updated")
            }
            .addOnFailureListener { e ->
                Log.e("PedidosViewModel", "Error updating request", e)
            }
    }
}