package com.example.myapplication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.myapplication.presentation.candidaturaVoluntario.CandidaturaVoluntarioScreen
import com.example.myapplication.presentation.login.AppNavigation
import com.example.myapplication.presentation.login.LoginScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
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
        fun registerUserFirebase(email: String, password: String, name: String) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Get the current user
                        val user = auth.currentUser
                        user?.let {
                            val uid = it.uid // Get the user's UID

                            // Save the name to Firestore
                            val db = Firebase.firestore
                            val userMap = hashMapOf(
                                "name" to name,
                                "sharedCarts" to emptyList<String>() // Initialize sharedCarts as an empty list
                            )

                            db.collection("users").document(uid)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Registration and Firestore Save Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error saving to Firestore", e)
                                    Toast.makeText(
                                        this,
                                        "Registration Successful, but Firestore Save Failed: ${e.message}",
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

        fun signInUserFirebase(email: String, password: String, navController: NavHostController) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("mainScreen")
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