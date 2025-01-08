package com.example.myapplication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.myapplication.domain.model.Households
import com.example.myapplication.domain.utils.CheckConnectionUtil
import com.example.myapplication.presentation.viewModels.AgregadosViewModel

@Composable
fun AgregadosScreen(navController: NavHostController) {
    val showNewHouseholdDialog = remember { mutableStateOf(false) }
    val viewModel: AgregadosViewModel = viewModel()
    val context = LocalContext.current

    var searchText by remember { mutableStateOf("") }

    var showSortDialog by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf("ID: Crescente") }

    LaunchedEffect(Unit) {
        viewModel.getHouseholds()
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
                    text = "Agregados Familiares",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
                // Icon "+" para adicionar agregado
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(1.dp, Color.Black, shape = CircleShape)
                        .clickable {
                            showNewHouseholdDialog.value = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Household",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // pop-up para adicionar novo Agregado
        if (showNewHouseholdDialog.value) {
            val newName = remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showNewHouseholdDialog.value = false },
                title = {
                    Text(
                        text = "Adicionar Novo Agregado",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column {
                        TextField(
                            value = newName.value,
                            onValueChange = { newName.value = it },
                            label = { Text("Nome") }
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = { showNewHouseholdDialog.value = false }) {
                        Text("Cancelar")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newHousehold = Households(
                                id = "",
                                name = newName.value,
                                visitors = emptyList()
                            )
                            if(CheckConnectionUtil.isConnected(context)) {
                                viewModel.addHousehold(newHousehold)
                                showNewHouseholdDialog.value = false
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                }
            )
        }

        Row (verticalAlignment = Alignment.CenterVertically) {
            // Campo para procura por nome
            TextField(
                value = searchText,
                onValueChange = { text ->
                    searchText = text
                    viewModel.filterHouseholds(text)
                },
                label = { Text("Pesquisar por nome") },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botão para ordenação
            Button(onClick = { showSortDialog = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Ord.",
                    modifier = Modifier.size(24.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Agregados
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.listHouseholds) { household ->
                ExpandableRowItemHousehold(navController, household)
            }
        }

        //region Ordenar
        if (showSortDialog) {
            AlertDialog(
                onDismissRequest = { showSortDialog = false },
                title = {
                    Text(text = "Ordenar Agregados")
                },
                text = {
                    Column {
                        listOf("ID: Crescente", "ID: Decrescente", "Nome: Crescente", "Nome: Decrescente").forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    selectedSortOption = option
                                }
                            ) {
                                RadioButton(
                                    selected = selectedSortOption == option,
                                    onClick = { selectedSortOption = option }
                                )
                                Text(text = option, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.sortHouseholds(selectedSortOption)
                            showSortDialog = false
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showSortDialog = false }) {
                        Text("Fechar")
                    }
                }
            )
        }
        //endregion
    }

}

@Composable
fun ExpandableRowItemHousehold(navController: NavHostController, household: Households) {
    val viewModel: AgregadosViewModel = viewModel()
    val context = LocalContext.current

    val isExpanded = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .clickable { isExpanded.value = !isExpanded.value }
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = household.id,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.3f)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = household.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.7f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { isExpanded.value = !isExpanded.value }
                )
            }

            // region Dropdown das opcoes (Ver Membros, Apagar)
            if (isExpanded.value) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Ver Membros
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "See More",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        navController.navigate("agregado_detalhes/${household.id}")
                                    }
                            )
                            Text(
                                text = "Ver Membros",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Apagar
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        showDeleteDialog.value = true
                                    }
                            )
                            Text(
                                text = "Apagar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
            }
            // endregion

            // region apagar agregado
            if (showDeleteDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog.value = false },
                    title = {
                        Text(
                            text = "Confirmar Exclusão",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Text(
                            text = "Tem certeza de que deseja apagar este agregado?",
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
                                    onClick = { showDeleteDialog.value = false }
                                ) {
                                    Text(text = "Cancelar")
                                }
                                Button(
                                    onClick = {
                                        if(CheckConnectionUtil.isConnected(context)) {
                                            viewModel.deleteHousehold(household.id)
                                            showDeleteDialog.value = false
                                        }
                                    }
                                ) {
                                    Text(text = "Apagar")
                                }
                            }
                        }
                    }
                )
            }
            //endregion
        }
    }
}