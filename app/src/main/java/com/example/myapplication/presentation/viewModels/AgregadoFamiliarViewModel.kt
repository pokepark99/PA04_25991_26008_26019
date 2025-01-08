package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Households
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HouseholdsViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private val _households = MutableLiveData<List<Households>>()
    val households: MutableLiveData<List<Households>> = _households

    private var allHouseholds = listOf<Households>()

    init {
        fetchHouseholds()
    }

    // Função para buscar todos os agregados do Firestore
    private fun fetchHouseholds() {
        firestore.collection("Households")
            .get()
            .addOnSuccessListener { result ->
                val fetchedHouseholds = result.documents.mapNotNull { documentToHousehold(it) }
                allHouseholds = fetchedHouseholds
                _households.value = fetchedHouseholds
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar agregados: $e")
                _households.value = emptyList()
            }
    }

    // Função para adicionar um novo agregado
    fun adicionarHousehold(id: String, visitantes: List<String>, notas: String) {
        val newHousehold = mapOf(
            "id" to id,
            "visitors" to visitantes,
            "notes" to notas
        )

        firestore.collection("Households")
            .add(newHousehold)
            .addOnSuccessListener {
                Log.d("Firestore", "Agregado adicionado com sucesso: ${it.id}")
                fetchHouseholds()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao adicionar agregado: $e")
            }
    }

    // Função para editar um agregado
    fun editarHousehold(householdId: String, visitantes: List<String>, notas: String) {
        val householdRef = firestore.collection("Households").document(householdId)

        val updatedData = mapOf(
            "visitors" to visitantes,
            "notes" to notas
        )

        householdRef.update(updatedData)
            .addOnSuccessListener {
                Log.d("Firestore", "Agregado atualizado com sucesso")
                fetchHouseholds()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao atualizar agregado: $e")
            }
    }

    // Função para remover um agregado
    fun removerHousehold(householdId: String) {
        val householdRef = firestore.collection("Households").document(householdId)

        householdRef.delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Agregado removido com sucesso")
                fetchHouseholds()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao remover agregado: $e")
            }
    }

    // Função para filtrar agregados pelo ID
    fun filterHouseholdsById(query: String) {
        _households.value = if (query.isEmpty()) {
            allHouseholds
        } else {
            allHouseholds.filter { it.id.contains(query, ignoreCase = true) }
        }
    }

    // Converte um documento Firestore em um objeto Households
    private fun documentToHousehold(document: DocumentSnapshot): Households? {
        val id = document.getString("id") ?: return null
        val visitors = document.get("visitors") as? List<String> ?: emptyList()
        val notes = document.getString("notes") ?: ""
        return Households(id = id, visitors = visitors, notes = notes)
    }
}
