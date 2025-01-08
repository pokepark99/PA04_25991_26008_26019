package com.example.myapplication.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class GraficosViewModel : ViewModel() {
    val barChartData = MutableStateFlow<Map<String, Float>>(emptyMap())

    private val firestore = Firebase.firestore

    fun fetchCountryVisitData() {
        viewModelScope.launch {
            val countriesList = firestore.collection("Countries").get()
            val visitorsList = firestore.collection("Visitors").get()
            val visitsList = firestore.collection("Visits").get()

            countriesList.addOnSuccessListener { countriesResult ->
                val countries = countriesResult.associateBy { it.id }

                visitorsList.addOnSuccessListener { visitorsResult ->
                    val visitors = visitorsResult.associateBy { it.id }

                    visitsList.addOnSuccessListener { visitsResult ->
                        val visitCounts = mutableMapOf<String, Float>()

                        for (visit in visitsResult) {
                            val visitorId = visit.getString("VisitorsId") ?: continue
                            val visitor = visitors[visitorId] ?: continue
                            val countryId = visitor.getString("CountriesId") ?: continue
                            val countryName = countries[countryId]?.getString("Name") ?: "Desconhecido"

                            visitCounts[countryName] = visitCounts.getOrDefault(countryName, 0f) + 1f
                        }

                        barChartData.value = visitCounts
                    }
                }
            }

        }
    }
}