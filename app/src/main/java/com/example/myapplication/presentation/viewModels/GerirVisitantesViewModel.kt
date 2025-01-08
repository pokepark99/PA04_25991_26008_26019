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
import java.util.Locale

class GerirVisitantesViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    var listVisitors by mutableStateOf<List<Visitors>>(emptyList())
    private var allVisitors = listOf<Visitors>()


    fun addVisitor(newVisitor: Visitors) {
        val visitorsRef = firestore.collection("Visitors")
        val formattedCountry = newVisitor.countriesId.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }

        firestore.collection("Countries")
            .whereEqualTo("Name", formattedCountry)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val countryId = if (querySnapshot.documents.isNotEmpty()) {
                    querySnapshot.documents[0].id
                } else {
                    val newCountryRef = firestore.collection("Countries").document()
                    newCountryRef.set(mapOf("Name" to formattedCountry))
                    newCountryRef.id
                }

                val visitorMap = mapOf(
                    "Name" to newVisitor.name,
                    "DOB" to newVisitor.dob,
                    "TaxNO" to newVisitor.taxNo,
                    "CountriesId" to countryId,
                    "NIF" to newVisitor.nif
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
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro a obter país", e)
            }
    }

    fun getVisitors() {
        firestore.collection("Visitors")
            .get()
            .addOnSuccessListener { result ->
                val visitorsList = result.documents.mapNotNull { document ->
                    documentToVisitor(document)
                }
                allVisitors = visitorsList
                listVisitors = visitorsList
            }
            .addOnFailureListener {
                listVisitors = emptyList()
            }
    }

    fun updateVisitor(visitorId: String, updatedVisitor: Visitors) {
        val visitorRef = firestore.collection("Visitors").document(visitorId)
        val formattedCountry = updatedVisitor.countriesId.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }

        firestore.collection("Countries")
            .whereEqualTo("Name", formattedCountry)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val countryId = if (querySnapshot.documents.isNotEmpty()) {
                    querySnapshot.documents[0].id
                } else {
                    val newCountryRef = firestore.collection("Countries").document()
                    newCountryRef.set(mapOf("Name" to formattedCountry))
                    newCountryRef.id
                }

                val visitorMap = mapOf(
                    "Name" to updatedVisitor.name,
                    "DOB" to updatedVisitor.dob,
                    "TaxNO" to updatedVisitor.taxNo,
                    "CountriesId" to countryId,
                    "NIF" to updatedVisitor.nif
                )

                visitorRef.set(visitorMap)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Firestore", "Visitante atualizado")
                        getVisitors()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Erro a atualizar visitante", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro a obter país", e)
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

    fun filterVisitors(query: String) {
        listVisitors = if (query.isEmpty()) {
            allVisitors
        } else {
            allVisitors.filter { visitor ->
                visitor.name.contains(query, ignoreCase = true)
            }
        }
    }

    fun sortVisitors(option: String) {
        listVisitors = when (option) {
            "ID: Crescente" -> listVisitors.sortedBy { it.id.lowercase() }
            "ID: Decrescente" -> listVisitors.sortedByDescending { it.id.lowercase() }
            "Nome: Crescente" -> listVisitors.sortedBy { it.name.lowercase() }
            "Nome: Decrescente" -> listVisitors.sortedByDescending { it.name.lowercase() }
            else -> listVisitors
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
        val nif = document.getLong("NIF")?.toLong() ?: 0

        return Visitors(
            id = document.id,
            name = name,
            dob = dob,
            taxNo = taxNo,
            countriesId = countriesId,
            nif = nif
        )
    }
}