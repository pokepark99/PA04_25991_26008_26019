package com.example.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.screens.CandidaturaVoluntarioScreen
import com.example.myapplication.presentation.screens.HorariosScreen
import com.example.myapplication.presentation.screens.LoginScreen
import com.example.myapplication.presentation.screens.MenuScreen
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
        composable("horarios/{isGestor}"){ backStackEntry ->
            val isGestor = backStackEntry.arguments?.getBoolean("isGestor")
            HorariosScreen(navController, isGestor!!)
        }
        // !! Arranjar depois de testes
        composable("menu/{isGestor}"){ backStackEntry ->
            val isGestor = backStackEntry.arguments?.getBoolean("isGestor")
            MenuScreen(navController, isGestor!!)
        }
        composable("inicio2") {
            MenuScreen(navController, true)
        }
    }
}