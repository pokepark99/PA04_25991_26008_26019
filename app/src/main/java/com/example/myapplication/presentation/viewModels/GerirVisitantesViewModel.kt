package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Visitors
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GerirVisitantesViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    var listVisitors by mutableStateOf<List<Visitors>>(emptyList())


    fun addVisitor(newVisitor: Visitors) {
        val visitorsRef = firestore.collection("Visitors")

        val visitorMap = mapOf(
            "Name" to newVisitor.name,
            "DOB" to newVisitor.dob,
            "TaxNO" to newVisitor.taxNo,
            "CountriesId" to newVisitor.countriesId
        )

        visitorsRef.add(visitorMap)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Adicionado o visitante: ${documentReference.id}")
                getVisitors()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro a adicionar visitante", e)
            }
    }

    fun getVisitors() {
        firestore.collection("Visitors")
            .get()
            .addOnSuccessListener { result ->
                val visitorsList = result.documents.mapNotNull { document ->
                    documentToVisitor(document)
                }
                listVisitors = visitorsList
            }
            .addOnFailureListener {
                listVisitors = emptyList()
            }
    }

    fun updateVisitor(visitorId: String, updatedVisitor: Visitors) {
        val visitorRef = firestore.collection("Visitors").document(visitorId)

        visitorRef.set(updatedVisitor)
            .addOnSuccessListener {
                Log.d("Firestore", "Visitante atualizado")
                getVisitors()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro a atualizar visitante", e)
            }
    }

    fun deleteVisitor(visitorId: String) {
        val visitorRef = firestore.collection("Visitors").document(visitorId)

        visitorRef.delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Visitante apagado")
                getVisitors()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro a apagar visitante", e)
            }
    }

    fun fetchCountryName(countryId: String, onResult: (String?) -> Unit) {
        firestore.collection("Countries")
            .document(countryId)
            .get()
            .addOnSuccessListener { document ->
                onResult(document.getString("Name"))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
fun documentToVisitor(document: DocumentSnapshot): Visitors? {
    val name = document.getString("Name") ?: ""
    val dob = document.getTimestamp("DOB") ?: Timestamp.now()
    val taxNo = document.getLong("TaxNO")?.toInt() ?: 0
    val countriesId = document.getString("CountriesId") ?: ""

    // Return the mapped Visitor object
    return Visitors(
        id = document.id,
        name = name,
        dob = dob,
        taxNo = taxNo,
        countriesId = countriesId
    )
}
}