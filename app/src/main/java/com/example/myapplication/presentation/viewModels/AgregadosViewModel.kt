package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Households
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AgregadosViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    var listHouseholds by mutableStateOf<List<Households>>(emptyList())
    private var allHouseholds = listOf<Households>()


    fun addHousehold(newHousehold: Households) {
        val householdsRef = firestore.collection("Households")

        val householdMap = mapOf(
            "Name" to newHousehold.name,
            "Visitors" to newHousehold.visitors
        )

        householdsRef.add(householdMap)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Adicionado o agregado: ${documentReference.id}")
                getHouseholds()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro a adicionar agregado", e)
            }

    }

    fun getHouseholds() {
        firestore.collection("Households")
            .get()
            .addOnSuccessListener { result ->
                val householdsList = result.documents.mapNotNull { document ->
                    documentToHousehold(document)
                }
                allHouseholds = householdsList
                listHouseholds = householdsList
            }
            .addOnFailureListener {
                listHouseholds = emptyList()
            }
    }

    fun deleteHousehold(householdId: String) {
        val visitorRef = firestore.collection("Households").document(householdId)

        visitorRef.delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Agregado apagado")
                getHouseholds()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro a apagar agregado", e)
            }
    }

    fun filterHouseholds(query: String) {
        listHouseholds = if (query.isEmpty()) {
            allHouseholds
        } else {
            allHouseholds.filter { household ->
                household.name.contains(query, ignoreCase = true)
            }
        }
    }

    fun sortHouseholds(option: String) {
        listHouseholds = when (option) {
            "ID: Crescente" -> listHouseholds.sortedBy { it.id.lowercase() }
            "ID: Decrescente" -> listHouseholds.sortedByDescending { it.id.lowercase() }
            "Nome: Crescente" -> listHouseholds.sortedBy { it.name.lowercase() }
            "Nome: Decrescente" -> listHouseholds.sortedByDescending { it.name.lowercase() }
            else -> listHouseholds
        }
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
}