package com.example.myapplication.presentation.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.myapplication.MainActivity
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email = mutableStateOf("")
    var password = mutableStateOf("")

    // Alteracao dos valores de email e password
    fun updateEmail(newEmail: String) {email.value = newEmail}
    fun updatePassword(newPassword: String) {password.value = newPassword}

    // Login com utilizador carregado
    fun loginCachedUser(navController: NavHostController, mainActivity: MainActivity){
        mainActivity.loginCachedUser(onSuccess = { navController.navigate("menu") })
    }

    // Login com Firebase
    fun loginUser(navController: NavHostController, mainActivity: MainActivity) {
        viewModelScope.launch { //usa a funcao na MainActivity
            mainActivity.loginUserFirebase(email.value, password.value, onSuccess = { navController.navigate("menu") })
        }
    }
}