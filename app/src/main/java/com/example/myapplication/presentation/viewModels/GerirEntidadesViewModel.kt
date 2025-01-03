package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Entities
import com.example.myapplication.domain.model.Visitors
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class GerirEntidadesViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    var listEntities by mutableStateOf<List<Entities>>(emptyList())
    private var allEntities = listOf<Entities>()


    fun addEntity(newEntity: Entities) {

        val entityMap = mapOf(
            "Name" to newEntity.name,
            "PhoneNo" to newEntity.phoneNo,
            "Email" to newEntity.email,
            "Address" to newEntity.address,
            "Notes" to newEntity.notes,
        )

        firestore.collection("Entities").add(entityMap)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Adicionado a entidade: ${documentReference.id}")
                getVisitors()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro a adicionar entidade", e)
            }
    }

    fun getVisitors() {
        firestore.collection("Entities")
            .get()
            .addOnSuccessListener { result ->
                val entitiesList = result.documents.mapNotNull { document ->
                    documentToEntity(document)
                }
                allEntities = entitiesList
                listEntities = entitiesList
            }
            .addOnFailureListener {
                listEntities = emptyList()
            }
    }

    fun updateEntity(entityId: String, updatedEntity: Entities) {

        val entityMap = mapOf(
            "Name" to updatedEntity.name,
            "PhoneNo" to updatedEntity.phoneNo,
            "Email" to updatedEntity.email,
            "Address" to updatedEntity.address,
            "Notes" to updatedEntity.notes,
        )

        firestore.collection("Entities").document(entityId).set(entityMap)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Entidade atualizada")
                getVisitors()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro a atualizar entidade", e)
            }
    }

    fun deleteEntity(entityId: String) {
        val entityRef = firestore.collection("Entities").document(entityId)

        entityRef.delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Entidade apagado")
                getVisitors()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro a apagar entidade", e)
            }
    }

    fun filterEntities(query: String) {
        listEntities = if (query.isEmpty()) {
            allEntities
        } else {
            allEntities.filter { entity ->
                entity.name.contains(query, ignoreCase = true)
            }
        }
    }

    fun sortEntities(option: String) {
        listEntities = when (option) {
            "ID: Crescente" -> listEntities.sortedBy { it.id.lowercase() }
            "ID: Decrescente" -> listEntities.sortedByDescending { it.id.lowercase() }
            "Nome: Crescente" -> listEntities.sortedBy { it.name.lowercase() }
            "Nome: Decrescente" -> listEntities.sortedByDescending { it.name.lowercase() }
            else -> listEntities
        }
    }

    fun documentToEntity(document: DocumentSnapshot): Entities? {
        val name = document.getString("Name") ?: ""
        val email = document.getString("Email") ?: ""
        val address = document.getString("Address") ?: ""
        val phoneNo = document.getLong("PhoneNo")?.toInt() ?: 0
        val notes = document.getString("Notes") ?: ""

        return Entities(
            id = document.id,
            name = name,
            phoneNo = phoneNo,
            email = email,
            address = address,
            notes = notes
        )
    }
}