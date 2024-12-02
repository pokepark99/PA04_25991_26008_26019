package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.myapplication.presentation.candidaturaVoluntario.CandidaturaVoluntarioScreen
import com.example.myapplication.presentation.login.LoginScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(){
    //exemplo de criar um objeto do tipo country
    //val countries = mutableListOf<Countries>()
    //Countries.addCountry(countries, 2, "Espanha")

    CandidaturaVoluntarioScreen()
}
