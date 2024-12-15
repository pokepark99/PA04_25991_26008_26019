package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.presentation.navigation.AppNavigation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val db = Firebase.firestore

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
                        val db = Firebase.firestore

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
                                    "Photo" to "",
                                    "State" to 0 // valor default
                                )
                                db.collection("Users").document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Registration Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error saving to Firestore", e)
                                        Toast.makeText(
                                            this,
                                            "Firestore Save Failed: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error checking country", e)
                                Toast.makeText(
                                    this,
                                    "Error checking country: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    // Handle registration failure
                    Toast.makeText(
                        this,
                        "Registration Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //login
    fun loginUserFirebase(email: String, password: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Login Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    onSuccess()
                } else {
                    Toast.makeText(
                        this,
                        "Login Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}