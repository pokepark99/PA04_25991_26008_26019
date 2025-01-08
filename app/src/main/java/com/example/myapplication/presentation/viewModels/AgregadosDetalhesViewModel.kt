package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Households
import com.example.myapplication.domain.model.Visitors
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AgregadosDetalhesViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    var listVisitors by mutableStateOf<List<Visitors>>(emptyList())
    var listHouseholdMembers by mutableStateOf<List<Visitors>>(emptyList())

    fun getHouseholdMembers(householdId: String) {
        firestore.collection("Households")
            .document(householdId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val households = documentToHousehold(document)
                    if (households != null) {
                        val visitorsIds = households.visitors
                        if (visitorsIds.isNotEmpty()) {
                            firestore.collection("Visitors")
                                .whereIn(FieldPath.documentId(), visitorsIds)
                                .get()
                                .addOnSuccessListener { results ->
                                    val listMembers = results.documents.mapNotNull { doc ->
                                        val member = documentToVisitor(doc)
                                        member?.let {
                                            Visitors(
                                                id = doc.id,
                                                name = it.name,
                                                countriesId = it.countriesId,
                                                phoneNo = it.phoneNo,
                                                nif = it.nif
                                            )
                                        }
                                    }
                                    listHouseholdMembers = listMembers
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("Firestore", "Erro a obter membros do agregado", exception)
                                    listHouseholdMembers = emptyList()
                                }
                        } else {
                            listHouseholdMembers = emptyList()
                        }
                    } else {
                        listHouseholdMembers = emptyList()
                    }
                } else {
                    listHouseholdMembers = emptyList()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro a obter agregados", exception)
                listHouseholdMembers = emptyList()
            }
    }

    fun removeHouseholdMember(householdId: String, visitorId: String) {
        firestore.collection("Households")
            .document(householdId)
            .update("Visitors", FieldValue.arrayRemove(visitorId))
        getHouseholdMembers(householdId)
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

    fun addMember(householdId: String, visitorId: String) {
        firestore.collection("Households")
            .document(householdId)
            .update("Visitors", FieldValue.arrayUnion(visitorId))
        getHouseholdMembers(householdId)
    }

    fun documentToHousehold(document: DocumentSnapshot): Households? {
        val name = document.getString("Name") ?: ""
        val visitors = document.get("Visitors") as? List<String> ?: emptyList()

        return Households(
            id = document.id,
            name = name,
            visitors = visitors
        )
    }

    fun documentToVisitor(document: DocumentSnapshot): Visitors? {
        val name = document.getString("Name") ?: ""
        val dob = document.getTimestamp("DOB") ?: Timestamp.now()
        val phoneNo = document.getLong("PhoneNo")?.toInt() ?: 0
        val countriesId = document.getString("CountriesId") ?: ""
        val nif = document.getLong("NIF")?.toInt() ?: 0

        return Visitors(
            id = document.id,
            name = name,
            dob = dob,
            phoneNo = phoneNo,
            countriesId = countriesId,
            nif = nif
        )
    }
}