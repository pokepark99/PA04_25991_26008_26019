package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Schedules
import com.example.myapplication.domain.model.Visitors
import com.example.myapplication.domain.model.Visits
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class VisitasViewModel : ViewModel() {
    private val _schedules = MutableStateFlow<List<Schedules>>(emptyList())
    val schedules: StateFlow<List<Schedules>> get() = _schedules

    private val _visits = MutableStateFlow<List<Visits>>(emptyList())
    val visits: StateFlow<List<Visits>> get() = _visits

    private val firestore = Firebase.firestore

    //busca os horarios ao firestore
    fun fetchSchedules(storeId: String) {
        Log.d("VisitasViewModel", "Fetching schedules for storeId: $storeId")
        firestore.collection("Schedules")
            .whereEqualTo("StoresId", storeId)
            .orderBy("DateStart", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    Log.d("VisitasViewModel", "Fetched ${snapshot.size()} schedules")
                    val _schedulesList = snapshot.documents.mapNotNull { document ->
                        Log.d("VisitasViewModel", "Document: ${document.data}")
                        val storeId = document.getString("StoresId") ?: return@mapNotNull null
                        val dateStart = document.getTimestamp("DateStart")
                        val dateEnd = document.getTimestamp("DateEnd")

                        if (dateStart != null && dateEnd != null) {
                            Schedules(
                                id = document.id,
                                storesId = storeId,
                                dateStart = dateStart,
                                dateEnd = dateEnd
                            )
                        } else {
                            null
                        }
                    }
                    _schedules.value = _schedulesList
                } else {
                    Log.d("VisitasViewModel", "No schedules found for storeId: $storeId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("VisitasViewModel", "Error fetching schedules: ", e)
            }
    }

    //busca as visitas para o horario selecionado
    fun fetchVisitsForSchedule(storeId: String, schedule: Schedules, callback: (List<Pair<Visits, Visitors>>) -> Unit) {
        firestore.collection("Visits")
            .whereEqualTo("StoresId", storeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("VisitasViewModel", "Error listening for visits updates", error)
                    callback(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val visitsList = snapshot.documents.mapNotNull { document ->
                        val visitorsId = document.getString("VisitorsId")
                        val date = document.getTimestamp("Date")
                        if (visitorsId != null && date != null &&
                            date.toDate().after(schedule.dateStart.toDate()) &&
                            date.toDate().before(schedule.dateEnd.toDate())
                        ) {
                            Visits(
                                id = document.id,
                                visitorsId = visitorsId,
                                date = date,
                                storesId = storeId
                            )
                        } else {
                            null
                        }
                    }

                    // Fetch visitor details for the filtered visits
                    val visitorIds = visitsList.map { it.visitorsId }
                    if (visitorIds.isNotEmpty()) {
                        firestore.collection("Visitors")
                            .whereIn(FieldPath.documentId(), visitorIds)
                            .get()
                            .addOnSuccessListener { visitorSnapshot ->
                                val visitorsMap = visitorSnapshot.documents.associateBy(
                                    { it.id },
                                    {
                                        Visitors(
                                            id = it.id,
                                            name = it.getString("Name") ?: "",
                                            dob = it.getTimestamp("DOB") ?: Timestamp.now(),
                                            taxNo = it.getLong("TaxNO")?.toInt() ?: 0,
                                            countriesId = it.getString("CountriesId") ?: ""
                                        )
                                    }
                                )

                                val combinedList = visitsList.mapNotNull { visit ->
                                    val visitor = visitorsMap[visit.visitorsId]
                                    if (visitor != null) {
                                        Pair(visit, visitor)
                                    } else {
                                        null
                                    }
                                }
                                callback(combinedList)
                            }
                            .addOnFailureListener { e ->
                                Log.e("VisitasViewModel", "Error fetching visitors for visits", e)
                                callback(emptyList())
                            }
                    } else {
                        callback(emptyList())
                    }
                }
            }
    }

    // procura visitantes existentes
    fun searchVisitors(query: String, callback: (List<Visitors>) -> Unit) {
        val lowerCaseQuery = query.lowercase(Locale.getDefault())

        firestore.collection("Visitors")
            .get()
            .addOnSuccessListener { snapshot ->
                val visitorsList = snapshot.documents.mapNotNull { document ->
                    val id = document.id
                    val name = document.getString("Name")
                    val dob = document.getTimestamp("DOB")
                    val taxNo = document.getLong("TaxNO")?.toInt()
                    val countriesId = document.getString("CountriesId")

                    if (name != null && dob != null && taxNo != null && countriesId != null) {
                        Visitors(id, name, dob, taxNo, countriesId)
                    } else {
                        null
                    }
                }.filter { visitor ->
                    // Perform client-side filtering for partial matches
                    visitor.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                            visitor.taxNo.toString().contains(lowerCaseQuery)
                }
                callback(visitorsList)
            }
            .addOnFailureListener { e ->
                Log.e("VisitasViewModel", "Error fetching visitors: ", e)
                callback(emptyList()) // Return empty list on failure
            }
    }

    // adicionar visita
    fun addVisit(visitorId: String, storeId: String?, callback: (Boolean) -> Unit) {
        if (storeId == null) {
            callback(false)
            return
        }

        val visitData = mapOf(
            "VisitorsId" to visitorId,
            "Date" to Timestamp.now(),
            "StoresId" to storeId
        )

        firestore.collection("Visits")
            .add(visitData)
            .addOnSuccessListener {
                Log.d("VisitasViewModel", "Visit successfully added")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.e("VisitasViewModel", "Error adding visit", e)
                callback(false)
            }
    }

    // adicionar um novo visitante
    fun addVisitor(name: String, dob: Timestamp, taxNo: Int, country: String, callback: (Boolean) -> Unit) {
        val formattedCountry = country.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }

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

                // Add the new visitor
                val visitorData = mapOf(
                    "Name" to name,
                    "DOB" to dob,
                    "TaxNO" to taxNo,
                    "CountriesId" to countryId
                )

                firestore.collection("Visitors")
                    .add(visitorData)
                    .addOnSuccessListener {
                        Log.d("VisitasViewModel", "Visitor successfully added")
                        callback(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("VisitasViewModel", "Error adding visitor", e)
                        callback(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("VisitasViewModel", "Error checking/adding country", e)
                callback(false)
            }
    }
}