package com.example.myapplication.presentation.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainActivity
import com.example.myapplication.domain.model.Users
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel(){
    private val firestore = Firebase.firestore

    //dados do utilizador
    var userData by mutableStateOf<Users?>(null)

    //obter dados do utilizador
    fun getCurrentUser(mainActivity: MainActivity, uid: String) {
        viewModelScope.launch {
            mainActivity.getCurrentUser(uid) { success ->
                userData = success
            }
        }
    }

    //obter email do utilizador
    fun getCurrentUserEmail(mainActivity: MainActivity): String? {
        return mainActivity.getCurrentUserEmail()
    }

    //buscar o nome do pais atraves do seu id
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

    //atualiza dados do utilizador
    fun updateUserProfile(
        phoneNo: Int,
        dob: String,
        city: String,
        countryName: String,
        nif: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        var auth = com.google.firebase.Firebase.auth
        val user = auth.currentUser

        if (user == null) {
            onError("Usuário não autenticado.")
            return
        }

        val uid = user.uid

        val formattedCountry = countryName.trim().lowercase().replaceFirstChar { it.uppercase() }

        // atribui o id do pais (cria se for necessario)
        firestore.collection("Countries")
            .whereEqualTo("Name", formattedCountry)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val countryId = if (querySnapshot.documents.isNotEmpty()) { //se o pais ja esta no database
                    querySnapshot.documents[0].id
                } else { // se o pais nao esta no database - cria
                    val newCountryRef = firestore.collection("Countries").document()
                    newCountryRef.set(mapOf("Name" to formattedCountry))
                    newCountryRef.id
                }

                val userUpdateMap = mapOf(
                    "PhoneNo" to phoneNo,
                    "DOB" to dob,
                    "City" to city,
                    "CountriesId" to countryId,
                    "NIF" to nif
                )

                firestore.collection("Users").document(uid)
                    .update(userUpdateMap)
                    .addOnSuccessListener {
                        Log.d("PerfilViewModel", "User profile updated successfully.")

                        // Atualiza dados locais
                        userData = userData?.copy(
                            phoneNo = phoneNo,
                            dob = dob,
                            city = city,
                            countriesId = countryId,
                            nif = nif
                        )

                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("PerfilViewModel", "Error updating user profile", e)
                        onError(e.message ?: "Erro desconhecido.")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PerfilViewModel", "Error checking/creating country", e)
                onError(e.message ?: "Erro ao verificar/criar o país.")
            }
    }
}