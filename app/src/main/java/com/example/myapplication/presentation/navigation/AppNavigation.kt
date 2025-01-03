package com.example.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.screens.CandidaturaVoluntarioScreen
import com.example.myapplication.presentation.screens.GerirEntidadesScreen
import com.example.myapplication.presentation.screens.GerirVisitantesScreen
import com.example.myapplication.presentation.screens.GerirVoluntariosScreen
import com.example.myapplication.presentation.screens.HorariosScreen
import com.example.myapplication.presentation.screens.LoginScreen
import com.example.myapplication.presentation.screens.MenuScreen
import com.example.myapplication.presentation.screens.PerfilScreen
import com.example.myapplication.presentation.screens.StockScreen
import com.example.myapplication.presentation.screens.VisitaLojaScreen
import com.example.myapplication.presentation.screens.VisitasScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("candidatura") {
            CandidaturaVoluntarioScreen(navController)
        }
        composable("horarios/{isGestor}") { backStackEntry ->
            val isGestor = backStackEntry.arguments?.getString("isGestor").toBoolean()
            HorariosScreen(navController, isGestor)
        }
        composable("visitasLoja"){
            VisitaLojaScreen(navController)
        }
        composable("visitas/{storeId}"){ backStackEntry ->
            val storeId = backStackEntry.arguments?.getString("storeId")
            VisitasScreen(navController, storeId)
        }
        composable("stock"){
            StockScreen(navController)
        }
        composable("visitantes"){
            GerirVisitantesScreen(navController)
        }
        composable("entidades"){
            GerirEntidadesScreen(navController)
        }
        composable("voluntarios"){
            GerirVoluntariosScreen(navController)
        }
        composable("menu"){
            MenuScreen(navController)
        }
    }
}