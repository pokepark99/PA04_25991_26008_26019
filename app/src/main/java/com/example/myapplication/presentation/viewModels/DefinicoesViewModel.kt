package com.example.myapplication.presentation.viewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.MainActivity
import com.example.myapplication.domain.model.Functionalities
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.io.File

class DefinicoesViewModel : ViewModel() {
    private val firestore = Firebase.firestore
    var listFunc by mutableStateOf<List<Functionalities>>(emptyList())
    var updateState by mutableStateOf(false)

    fun getFuncionalidades(mainActivity: MainActivity) {
        viewModelScope.launch {
            mainActivity.getFuncionalidades() { success ->
                listFunc = success
            }
        }
    }
    fun toggleFuncionalidade(id: String, newState: Boolean) {
        listFunc = listFunc.map { func ->
            if (func.Id == id) func.copy(State = newState) else func
        }
    }

    fun updateFuncionalidades() {
        updateState = false
        viewModelScope.launch {
            listFunc.forEach { functionality ->
                firestore.collection("Functionalities").document(functionality.Id)
                    .update("state", functionality.State)
            }
            updateState = true
        }
    }

    fun cancelVoluntario() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid!!
            firestore.collection("Users").document(uid)
                .update("State", 2)
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            val filePath = context.filesDir.absolutePath + "/profile.jpg"
            val file = File(filePath)
            if (file.exists()) { file.delete() }
            Firebase.auth.signOut()
        }
    }
}