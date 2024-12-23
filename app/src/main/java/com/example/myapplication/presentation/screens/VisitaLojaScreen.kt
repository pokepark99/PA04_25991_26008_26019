package com.example.myapplication.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.presentation.viewModels.VisitaLojaViewModel

@Composable
fun VisitaLojaScreen(navController: NavHostController){
    val viewModel: VisitaLojaViewModel = viewModel()

    //obter as lojas encontradas em VisitaLojaViewModel
    val lojas = viewModel.lojas.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier.padding(16.dp)) {
            // Criar um botao por loja
            lojas.value.forEach { store ->
                Button(
                    onClick = {
                        // Navegar para "visitas"
                        navController.navigate("visitas/${store.id}")
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(text = store.name) // Usar o noma da loja como nome
                }
            }
        }
    }
}
