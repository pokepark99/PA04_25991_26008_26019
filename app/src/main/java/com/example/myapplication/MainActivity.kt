package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Functionalities
import com.example.myapplication.domain.model.Users
import com.example.myapplication.presentation.navigation.AppNavigation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore

        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }

    //registar um novo voluntario
    fun registerUserFirebase(
        email: String,
        password: String,
        city: String,
        country: String,
        dob: String,
        name: String,
        contact : Number,
        nif:Long,
        onSuccess: () -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Get the current user
                    val user = auth.currentUser
                    user?.let {
                        val uid = it.uid // Get the user's UID
                        // Save the name to Firestore

                        //formatar o nome do pais
                        val formattedCountry =
                            country.lowercase().replaceFirstChar { it.uppercase() }

                        //verificar se o pais ja esta na firestore
                        db.collection("Countries")
                            .whereEqualTo("Name", formattedCountry)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val countryId = if (querySnapshot.documents.isNotEmpty()) {
                                    //o pais ja esta no firestore
                                    querySnapshot.documents[0].id
                                } else {
                                    //o pais nao existe na firestore
                                    val newCountryRef = db.collection("Countries").document()
                                    newCountryRef.set(mapOf("Name" to formattedCountry))
                                    newCountryRef.id
                                }
                                // Guardar info do utilizador no Firestore
                                val userMap = hashMapOf(
                                    "Admin" to false, //default
                                    "City" to city,
                                    "CountriesId" to countryId,
                                    "DOB" to dob,
                                    "Name" to name,
                                    "PhoneNo" to contact,
                                    "NIF" to nif,
                                    "Photo" to "",
                                    "State" to 0 // valor default
                                )
                                db.collection("Users").document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Registo com sucesso",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error saving to Firestore", e)
                                        Toast.makeText(
                                            this,
                                            "Erro a guardar na firestore: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error checking country", e)
                                Toast.makeText(
                                    this,
                                    "Erro a obter país: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    // Handle registration failure
                    Toast.makeText(
                        this,
                        "Erro a registar: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //login com utilizador carregado
    fun loginCachedUser(onSuccess: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            onSuccess()
        }
    }

    //login
    fun loginUserFirebase(email: String, password: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Entrada com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    onSuccess()
                } else {
                    Toast.makeText(
                        this,
                        "Erro ao entrar: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // Obtem funcionalidades
    fun getFuncionalidades(onSuccess: (List<Functionalities>) -> Unit) {
        db.collection("Functionalities")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val functionalities = result.documents.mapNotNull {
                        it.toObject(Functionalities::class.java)?.apply { Id = it.id }
                    }
                    onSuccess(functionalities)
                } else {
                    onSuccess(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro:", exception)
                onSuccess(emptyList())
            }
    }

    // Obtem dados do utilizador
    fun getCurrentUser(uid: String, onSuccess: (Users?) -> Unit) {
            db.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        //val user = document.toObject(Users::class.java)
                        val user = documentToUser(document)
                        onSuccess(user)
                    } else {
                        onSuccess(null)
                    }
                }
                .addOnFailureListener {
                    onSuccess(null)
                }
    }

    fun documentToUser(document: DocumentSnapshot): Users? {
        val name = document.getString("Name") ?: ""
        val dob = document.getString("DOB") ?: ""
        val countriesId = document.getString("CountriesId") ?: ""
        val admin = document.getBoolean("Admin") ?: false
        val state = document.getLong("State") ?: 0
        val photo = document.getString("Photo") ?: ""
        val city = document.getString("City") ?: ""
        val nif = document.getLong("NIF") ?: 0
        val phoneNo = document.getLong("PhoneNo") ?: 0

        // Return the mapped Visitor object
        return Users(
            id = document.id,
            name = name,
            dob = dob,
            countriesId = countriesId,
            admin = admin,
            state = state.toInt(),
            photo = photo,
            city = city,
            nif = nif,
            phoneNo = phoneNo.toInt()
        )
    }
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}