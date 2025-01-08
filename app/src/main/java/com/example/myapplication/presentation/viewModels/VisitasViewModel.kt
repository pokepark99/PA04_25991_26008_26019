package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Schedules
import com.example.myapplication.domain.model.Visitors
import com.example.myapplication.domain.model.Visits
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import java.util.Locale

class VisitasViewModel : ViewModel() {
    private val _schedules = MutableStateFlow<List<Schedules>>(emptyList())
    val schedules: StateFlow<List<Schedules>> get() = _schedules

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
                        val open = document.getBoolean("Open")

                        if (dateStart != null && dateEnd != null && open != null) {
                            Schedules(
                                id = document.id,
                                storeId = storeId,
                                dateStart = dateStart,
                                dateEnd = dateEnd,
                                open = open
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

                    // informacao do visitante
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
                                            phoneNo = it.getLong("PhoneNo")?.toInt() ?: 0,
                                            countriesId = it.getString("CountriesId") ?: "",
                                            nif = it.getLong("NIF")?.toInt() ?: 0
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
                    val phoneNo = document.getLong("PhoneNo")?.toInt()
                    val countriesId = document.getString("CountriesId")

                    if (name != null && dob != null && phoneNo != null && countriesId != null) {
                        Visitors(id, name, dob, phoneNo, countriesId)
                    } else {
                        null
                    }
                }.filter { visitor ->
                    visitor.name.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                            visitor.phoneNo.toString().contains(lowerCaseQuery)
                }
                callback(visitorsList)
            }
            .addOnFailureListener { e ->
                Log.e("VisitasViewModel", "Error fetching visitors: ", e)
                callback(emptyList())
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
    fun addVisitor(name: String, dob: Timestamp, phoneNo: Int, country: String, nif:Long, callback: (Boolean) -> Unit) {
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

                val visitorData = mapOf(
                    "Name" to name,
                    "DOB" to dob,
                    "PhoneNo" to phoneNo,
                    "CountriesId" to countryId,
                    "NIF" to nif
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

    // apagar visita
    fun deleteVisit(visitId: String) {
        val visitDel = FirebaseFirestore.getInstance().collection("Visits").document(visitId)
        Log.d("VisitasViewModel", "Attempting to delete document: ${visitDel.path}")

        visitDel.delete()
            .addOnSuccessListener {
                Log.d("VisitasViewModel", "Visit deleted successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("VisitasViewModel", "Error deleting visit", exception)
            }
    }

    // busca o agregado de um visitante
    fun fetchHouseholdDetails(visitorId: String, onResult: (List<Pair<String, String>>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("Households")
            .whereArrayContains("Visitors", visitorId)
            .get()
            .addOnSuccessListener { documents ->
                val familyDetails = mutableListOf<Pair<String, String>>()
                val visitorRefs = documents.flatMap { document ->
                    (document.get("Visitors") as? List<String>)?.filter { it != visitorId } ?: emptyList()
                }

                if (visitorRefs.isEmpty()) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                var fetchedCount = 0
                visitorRefs.forEach { visitorRef ->
                    db.collection("Visitors").document(visitorRef)
                        .get()
                        .addOnSuccessListener { visitorDoc ->
                            val name = visitorDoc.getString("Name") ?: "Desconhecido"
                            val phoneNo = visitorDoc.getLong("PhoneNo")?.toString() ?: "Desconhecido"
                            familyDetails.add(Pair(phoneNo, name))
                        }
                        .addOnFailureListener {
                            familyDetails.add(Pair("Desconhecido", "Desconhecido"))
                        }
                        .addOnCompleteListener {
                            fetchedCount++
                            if (fetchedCount == visitorRefs.size) {
                                onResult(familyDetails)
                            }
                        }
                }
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // busca o nome de um pais atraves de o seu ID
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

    // atualiza os dados de uma visita
    fun updateVisit(
        visitId: String,
        updatedFields: Map<String, Any>
    ) {
        firestore.collection("Visits").document(visitId)
            .update(updatedFields)
            .addOnSuccessListener {
                Log.d("VisitasViewModel", "Visit updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("VisitasViewModel", "Error updating visit", e)
            }
    }

    // obtem todos os visitantes
    fun fetchAllVisitors(callback: (List<Visitors>) -> Unit) {
        firestore.collection("Visitors")
            .get()
            .addOnSuccessListener { snapshot ->
                val visitorsList = snapshot.documents.mapNotNull { document ->
                    val id = document.id
                    val name = document.getString("Name")
                    val dob = document.getTimestamp("DOB")
                    val phoneNo = document.getLong("PhoneNo")?.toInt()
                    val countriesId = document.getString("CountriesId")
                    val nif = document.getLong("NIF")?.toInt()

                    if (name != null && dob != null && phoneNo != null && countriesId != null && nif != null) {
                        Visitors(id, name, dob, phoneNo, countriesId, nif)
                    } else {
                        null
                    }
                }
                callback(visitorsList)
            }
            .addOnFailureListener { e ->
                Log.e("VisitasViewModel", "Error fetching visitors: ", e)
                callback(emptyList())
            }
    }
    //adicionar infração
    fun addInfraction(visitorId: String, severity: Int) {
        val infractionsRef = firestore.collection("Infractions_")

        // adicionar infracao
        val infractionData = mapOf(
            "VisitorsId" to visitorId,
            "Date" to Timestamp.now(),
            "Severity" to severity
        )

        if (severity == 0) {
            // verifica quantas infrações graves existem
            infractionsRef
                .whereEqualTo("VisitorsId", visitorId)
                .whereEqualTo("Severity", 0)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.size() == 2) {
                        // se ja existir 2 (e estamos a tentar adicionar a 3a), apaga as 2 existentes e adiciona uma muito grave
                        val batch = firestore.batch()
                        querySnapshot.documents.forEach { document ->
                            batch.delete(document.reference)
                        }

                        // adicionar uma infração muito grave
                        val upgradedInfraction = mapOf(
                            "VisitorsId" to visitorId,
                            "Date" to Timestamp.now(),
                            "Severity" to 1
                        )
                        //adiciona a infração muito grave
                        infractionsRef.add(upgradedInfraction)
                            .addOnSuccessListener {
                                Log.d("VisitasViewModel", "Upgraded infraction added successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("VisitasViewModel", "Error adding upgraded infraction", e)
                            }

                        // apaga as 2 infrações graves existentes
                        batch.commit()
                            .addOnSuccessListener {
                                Log.d("VisitasViewModel", "Old infractions deleted successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("VisitasViewModel", "Error deleting old infractions", e)
                            }
                    } else {
                        // Adiciona a defesa muito grave
                        infractionsRef.add(infractionData)
                            .addOnSuccessListener {
                                Log.d("VisitasViewModel", "Infraction added successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("VisitasViewModel", "Error adding infraction", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("VisitasViewModel", "Error checking severe infractions", e)
                }
        } else {
            // Add a "really severe" infraction directly
            infractionsRef.add(infractionData)
                .addOnSuccessListener {
                    Log.d("VisitasViewModel", "Really severe infraction added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("VisitasViewModel", "Error adding really severe infraction", e)
                }
        }
    }

    //busca infracoes ao firestore
    fun fetchInfractions(visitorId: String, callback: (List<Pair<Int, Timestamp>>) -> Unit) {
        val now = Timestamp.now()

        firestore.collection("Infractions_")
            .whereEqualTo("VisitorsId", visitorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("VisitasViewModel", "Error listening for infractions updates", error)
                    callback(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val infractions = mutableListOf<Pair<Int, Timestamp>>()
                    val batch = firestore.batch()

                    snapshot.documents.forEach { document ->
                        val severity = document.getLong("Severity")?.toInt()
                        val date = document.getTimestamp("Date")

                        if (severity != null && date != null) {
                            val expirationMonths = when (severity) {
                                0 -> 2
                                1 -> 4
                                else -> 0 // outras severities sao ignoradas
                            }

                            val expirationDate = Calendar.getInstance().apply {
                                time = date.toDate()
                                add(Calendar.MONTH, expirationMonths)
                            }.time

                            //tempo de expirar ainda não passou
                            if (expirationDate.after(now.toDate())) {
                                // infração válida
                                infractions.add(Pair(severity, date))
                            } else { //tempo para expirar já passou
                                // Infração adicionada para ser removida
                                batch.delete(document.reference)
                            }
                        }
                    }

                    // Apaga todas as infrações expiradas
                    batch.commit().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("VisitasViewModel", "Expired infractions deleted successfully")
                        } else {
                            Log.e("VisitasViewModel", "Failed to delete expired infractions", task.exception)
                        }
                    }
                    callback(infractions)
                } else {
                    callback(emptyList())
                }
            }
    }
}