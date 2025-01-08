package com.example.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.screens.CandidaturaHorarioScreen
import com.example.myapplication.presentation.screens.CandidaturaVoluntarioScreen
import com.example.myapplication.presentation.screens.DefinicoesScreen
import com.example.myapplication.presentation.screens.DoacoesScreen
import com.example.myapplication.presentation.screens.GerirEntidadesScreen
import com.example.myapplication.presentation.screens.HouseholdsScreen
import com.example.myapplication.presentation.screens.GerirVisitantesScreen
import com.example.myapplication.presentation.screens.GerirVoluntariosScreen
import com.example.myapplication.presentation.screens.GraficosScreen
import com.example.myapplication.presentation.screens.HorariosScreen
import com.example.myapplication.presentation.screens.LoginScreen
import com.example.myapplication.presentation.screens.MenuScreen
import com.example.myapplication.presentation.screens.PedidosScreen
import com.example.myapplication.presentation.screens.PerfilScreen
import com.example.myapplication.presentation.screens.StockScreen
import com.example.myapplication.presentation.screens.VisitaLojaScreen
import com.example.myapplication.presentation.screens.VisitasScreen
import com.example.myapplication.presentation.viewModels.HouseholdsViewModel

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
        composable("pedidos"){
            PedidosScreen(navController)
        }
        composable("doacoes"){
            DoacoesScreen(navController)
        }
        composable("graficos"){
            GraficosScreen(navController)
        }
        composable("user_settings/{userId}"){backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            PerfilScreen(navController, userId)
        }
        composable("settings/{isGestor}") { backStackEntry ->
            val isGestor = backStackEntry.arguments?.getString("isGestor").toBoolean()
            DefinicoesScreen(navController, isGestor)
        }
        composable("candidatura_horario"){
            CandidaturaHorarioScreen(navController)
        }
        composable("candidatura_horario/{scheduleId}"){backStackEntry ->
            val scheduleId = backStackEntry.arguments?.getString("scheduleId")
            if (scheduleId != null) {
                CandidaturaHorarioScreen(navController, scheduleId)
            }
        }
        composable("menu"){
            MenuScreen(navController)
        }

        composable("agregado") {
            val viewModel = HouseholdsViewModel()
            HouseholdsScreen(navController, viewModel)
        }

    }
}