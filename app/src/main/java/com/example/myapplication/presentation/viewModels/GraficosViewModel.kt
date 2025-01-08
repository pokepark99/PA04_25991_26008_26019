package com.example.myapplication.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class CountryVisit(val countryName: String, val visitCount: Int)

class GraficosViewModel : ViewModel() {
    val countryVisits = MutableStateFlow<List<CountryVisit>>(emptyList())

    private val firestore = Firebase.firestore

    fun fetchCountryVisitData() {
        viewModelScope.launch {
            val countriesList = firestore.collection("Countries").get()
            val visitorsList = firestore.collection("Visitors").get()
            val visitsList = firestore.collection("Visits").get()
        }
    }
}