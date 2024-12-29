package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Stores
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VisitaLojaViewModel : ViewModel() {
    private val lojasList = MutableStateFlow<List<Stores>>(emptyList())
    val lojas: StateFlow<List<Stores>> = lojasList

    private val firestore = Firebase.firestore

    init {
        fetchLojas()
    }

    //buscar todas as lojas no firestore
    private fun fetchLojas() {
        firestore.collection("Stores")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("VisitaLojaViewModel", "Listen failed.", e)
                    return@addSnapshotListener //atualiza quando ha atualizacoes aos stores
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val _lojasList = snapshot.documents.mapNotNull { document ->
                        val name = document.getString("Name") ?: ""
                        val location = document.getString("Location") ?: ""
                        val id = document.id //id da loja
                        Stores(id, name, location)
                    }
                    lojasList.value = _lojasList //obtem as lojas
                } else {
                    Log.d("VisitaLojaViewModel", "Nenhuma loja encontrada")
                }
            }
    }
}