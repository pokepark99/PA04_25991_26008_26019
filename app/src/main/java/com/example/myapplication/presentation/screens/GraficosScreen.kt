package com.example.myapplication.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.presentation.viewModels.GraficosViewModel


@Composable
fun GraficosScreen(navController: NavHostController) {
    val viewModel: GraficosViewModel = viewModel()
    val countryVisits by viewModel.countryVisits.collectAsState()
}