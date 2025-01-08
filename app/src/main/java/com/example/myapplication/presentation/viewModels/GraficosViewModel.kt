package com.example.myapplication.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class GraficosViewModel : ViewModel() {
    val pieChartData = MutableStateFlow<Map<String, Float>>(emptyMap())
    val barGraphData = MutableStateFlow<List<Int>>(emptyList())

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

                        pieChartData.value = visitCounts
                    }
                }
            }

        }
    }

    fun fetchMonthlyVisitData() {
        viewModelScope.launch {
            val visitsCollection = firestore.collection("Visits")
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            visitsCollection.get()
                .addOnSuccessListener { documents ->
                    val monthlyCounts = IntArray(12)

                    documents.forEach { document ->
                        val timestamp = document.getTimestamp("Date")?.toDate()
                        if (timestamp != null) {
                            val calendar = Calendar.getInstance().apply { time = timestamp }
                            val year = calendar.get(Calendar.YEAR)
                            val month = calendar.get(Calendar.MONTH) // 0 = Janeiro

                            if (year == currentYear) {
                                monthlyCounts[month]++
                            }
                        }
                    }

                    barGraphData.value = monthlyCounts.toList()
                }

        }
    }
}