package com.example.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.screens.CandidaturaVoluntarioScreen
import com.example.myapplication.presentation.screens.LoginScreen
import com.example.myapplication.presentation.screens.VisitaLojaScreen
import com.example.myapplication.presentation.screens.VisitasScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") {
            LoginScreen(navController)
        }
        composable("candidatura") {
            CandidaturaVoluntarioScreen(navController)
        }
        composable("visitasLoja"){
            VisitaLojaScreen(navController)
        }
        composable("visitas/{storeId}"){ backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")
            VisitasScreen(navController, storeId)
        }
    }
}