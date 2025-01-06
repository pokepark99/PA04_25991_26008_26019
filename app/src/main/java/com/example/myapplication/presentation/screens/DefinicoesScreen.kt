package com.example.myapplication.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.MainActivity
import com.example.myapplication.domain.model.Functionalities
import com.example.myapplication.presentation.viewModels.DefinicoesViewModel

@Composable
fun DefinicoesScreen(navController: NavHostController, isGestor: Boolean){
    val mainActivity = LocalContext.current as MainActivity
    val viewModel: DefinicoesViewModel = viewModel()
    val context = LocalContext.current

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    if (isGestor){
        LaunchedEffect(Unit) {
            viewModel.getFuncionalidades(mainActivity)
        }
    }
    LaunchedEffect(viewModel.updateState) {
        if (viewModel.updateState) {
            Toast.makeText(context, "Funcionalidades Atualizadas", Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B6089))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon para voltar atras
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )
                // Titulo
                Text(
                    text = "Definições",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
            }
        }

        if (isGestor) {
            // Lista de Definições
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.listFunc) { func ->
                    FunctionalityRow(func) { isActive ->
                        viewModel.toggleFuncionalidade(func.Id, isActive)
                    }
                }
            }
            Button(
                onClick = { viewModel.updateFuncionalidades() },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Guardar")
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray,
                thickness = 1.dp
            )
        }

        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B6089), contentColor = Color.White)
        ) {
            Text("Sair")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { showCancelDialog = true },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
        ) {
            Text("Cancelar Conta")
        }

        // region Logout
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = "Confirmar Logout",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "Tem certeza de que deseja sair?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { showLogoutDialog = false }
                            ) {
                                Text(text = "Cancelar")
                            }
                            Button(
                                onClick = {
                                    viewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            ) {
                                Text(text = "Confirmar")
                            }
                        }
                    }
                }
            )
        }
        //endregion

        // region Cancelar Conta
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = {
                    Text(
                        text = "Confirmar Cancelamento",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "Tem certeza de que deseja cancelar a sua conta?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { showCancelDialog = false }
                            ) {
                                Text(text = "Cancelar")
                            }
                            Button(
                                onClick = {
                                    viewModel.cancelVoluntario()
                                    navController.navigate("login")     {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                            ) {
                                Text(text = "Confirmar")
                            }
                        }
                    }
                }
            )
        }
        //endregion

    }
}

@Composable
fun FunctionalityRow(functionality: Functionalities, onToggleChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column (modifier = Modifier.weight(1f)) {
            Text(text = functionality.Name, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Text(text = functionality.Description)
        }
        Switch(
            checked = functionality.State,
            onCheckedChange = onToggleChange
        )
    }
}