package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Entities
import com.example.myapplication.domain.model.Users
import com.example.myapplication.domain.model.Visitors
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class GerirVoluntariosViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    var listUsers by mutableStateOf<List<Users>>(emptyList())
    private var allUsers = listOf<Users>()

    fun getUsers() {
        firestore.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                val visitorsList = result.documents.mapNotNull { document ->
                    documentToUser(document)
                }
                allUsers = visitorsList
                listUsers = visitorsList
            }
            .addOnFailureListener {
                listUsers = emptyList()
            }
    }

    fun updateUser(userId: String, updatedUser: Users, firebaseUserId: String) {

        if (userId == firebaseUserId){
            Log.d("Erro", "Um voluntáro não pode alterar o seu próprio estado")
            return
        }

        val userMap = mapOf(
            "Name" to updatedUser.name,
            "DOB" to updatedUser.dob,
            "CountriesId" to updatedUser.countriesId,
            "Admin" to updatedUser.admin,
            "State" to updatedUser.state,
            "Photo" to updatedUser.photo,
            "City" to updatedUser.city,
            "PhoneNo" to updatedUser.phoneNo
        )

        firestore.collection("Users").document(userId).set(userMap)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Voluntário atualizado")
                getUsers()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro a atualizar voluntário", e)
            }
    }

    fun filterUsers(query: String) {
        listUsers = if (query.isEmpty()) {
            allUsers
        } else {
            allUsers.filter { visitor ->
                visitor.name.contains(query, ignoreCase = true)
            }
        }
    }

    fun filterUsersState(estados: Set<String>) {
        val stateMap = mapOf(
            "Pendentes" to 0,
            "Ativos" to 1,
            "Cancelados" to 2
        )

        val selectedStates = estados.mapNotNull { stateMap[it] }.toSet()

        listUsers = allUsers.filter { user ->
            user.state in selectedStates
        }
    }

    fun sortUsers(option: String) {
        listUsers = when (option) {
            "ID: Crescente" -> listUsers.sortedBy { it.id.lowercase() }
            "ID: Decrescente" -> listUsers.sortedByDescending { it.id.lowercase() }
            "Nome: Crescente" -> listUsers.sortedBy { it.name.lowercase() }
            "Nome: Decrescente" -> listUsers.sortedByDescending { it.name.lowercase() }
            else -> listUsers
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

    fun documentToUser(document: DocumentSnapshot): Users? {
        val name = document.getString("Name") ?: ""
        val dob = document.getString("DOB") ?: ""
        val countriesId = document.getString("CountriesId") ?: ""
        val admin = document.getBoolean("Admin") ?: false
        val state = document.getLong("State")?.toInt() ?: 0
        val photo = document.getString("Photo") ?: ""
        val city = document.getString("City") ?: ""
        val phoneNo = document.getLong("PhoneNo")?.toInt() ?: 0

        return Users(
            id = document.id,
            name = name,
            dob = dob,
            countriesId = countriesId,
            admin = admin,
            state = state,
            photo = photo,
            city = city,
            phoneNo = phoneNo
        )
    }

}