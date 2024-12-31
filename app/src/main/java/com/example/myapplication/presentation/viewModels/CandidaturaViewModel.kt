package com.example.myapplication.presentation.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.myapplication.MainActivity
import kotlinx.coroutines.launch

class CandidaturaVoluntarioViewModel : ViewModel() {

    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var dob = mutableStateOf("")
    var nif = mutableStateOf("")
    var city = mutableStateOf("")
    var country = mutableStateOf("")
    var contact = mutableStateOf("")
    var password = mutableStateOf("")

    // atualizar os dados
    fun updateName(newValue: String) { name.value = newValue }
    fun updateEmail(newValue: String) { email.value = newValue }
    fun updateDob(newValue: String) { dob.value = newValue }
    fun updateNif(newValue: String) { nif.value = newValue }
    fun updateCity(newValue: String) { city.value = newValue }
    fun updateCountry(newValue: String) { country.value = newValue }
    fun updateContact(newValue: String) { contact.value = newValue }
    fun updatePassword(newValue: String) { password.value = newValue }

    // SignUp
    fun registerVolunteer(navController: NavHostController, mainActivity: MainActivity) {
        viewModelScope.launch { //usa a funcao na MainActivity
            mainActivity.registerUserFirebase(email.value, password.value, city.value, country.value, dob.value, name.value, contact.value.toInt(), onSuccess = { navController.navigate("inicio") })
        }
    }
}
