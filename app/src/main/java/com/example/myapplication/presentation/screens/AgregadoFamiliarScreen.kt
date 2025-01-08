package com.example.myapplication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.domain.model.Households
import com.example.myapplication.presentation.viewModels.HouseholdsViewModel

@Composable
fun GerirHouseholdsScreen(navController: NavController, viewModel: HouseholdsViewModel) {
    var showNewHouseholdDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf<Households?>(null) }
    var showRemoveDialog by remember { mutableStateOf<Households?>(null) }
    var showEditDialog by remember { mutableStateOf<Households?>(null) }
    var searchText by remember { mutableStateOf("") }

    val households by viewModel.households.observeAsState(emptyList())

    val filteredHouseholds = if (searchText.isEmpty()) households else households.filter {
        it.id.contains(searchText, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title = "Gerir Agregados", onAddClick = { showNewHouseholdDialog = true })

        if (showNewHouseholdDialog) {
            NewHouseholdDialog(
                onDismiss = { showNewHouseholdDialog = false },
                onConfirm = { id, visitors, notes ->
                    viewModel.adicionarHousehold(id, visitors, notes)
                    showNewHouseholdDialog = false
                }
            )
        }

        showDetailsDialog?.let { household ->
            DetailsDialog(household = household, onDismiss = { showDetailsDialog = null })
        }

        showRemoveDialog?.let { household ->
            RemoveDialog(household = household, onDismiss = { showRemoveDialog = null }, onConfirm = {
                viewModel.removerHousehold(household.id)
                showRemoveDialog = null
            })
        }

        showEditDialog?.let { household ->
            EditHouseholdDialog(
                household = household,
                onDismiss = { showEditDialog = null },
                onConfirm = { _, visitors, notes ->
                    viewModel.editarHousehold(household.id, visitors, notes)
                    showEditDialog = null
                }
            )
        }

        SearchBar(searchText, onTextChange = { searchText = it })

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredHouseholds) { household ->
                ExpandableRowItemHousehold(
                    household,
                    viewModel,
                    navController,
                    onDetailsClick = { showDetailsDialog = household },
                    onRemoveClick = { showRemoveDialog = household },
                    onEditClick = { showEditDialog = household }
                )
            }
        }
    }
}

@Composable
fun DetailsDialog(household: Households, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalhes do Agregado ${household.id}") },
        text = {
            Column {
                Text("ID: ${household.id}")
                Text("Visitantes: ${household.visitors.joinToString(", ")}")
                Text("Notas: ${household.notes}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

@Composable
fun RemoveDialog(household: Households, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remover Agregado") },
        text = {
            Column {
                Text("Tem certeza que deseja remover o agregado com ID: ${household.id}?")
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Remover")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditHouseholdDialog(
    household: Households,
    onDismiss: () -> Unit,
    onConfirm: (String, List<String>, String) -> Unit
) {
    var visitors by remember { mutableStateOf(household.visitors.joinToString(", ")) }
    var notes by remember { mutableStateOf(household.notes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Agregado ${household.id}") },
        text = {
            Column {
                Text("ID: ${household.id}", style = MaterialTheme.typography.bodyLarge)

                TextField(
                    value = visitors,
                    onValueChange = { visitors = it },
                    label = { Text("Visitantes (separados por vírgula)") }
                )

                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(household.id, visitors.split(",").map { it.trim() }, notes)
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun TopBar(title: String, onAddClick: () -> Unit) {
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
            Text(
                text = title,
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Start
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, shape = CircleShape)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Household",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun SearchBar(searchText: String, onTextChange: (String) -> Unit) {
    TextField(
        value = searchText,
        onValueChange = onTextChange,
        label = { Text("Pesquisar por ID") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Composable
fun NewHouseholdDialog(onDismiss: () -> Unit, onConfirm: (String, List<String>, String) -> Unit) {
    var id by remember { mutableStateOf("") }
    var visitors by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Novo Agregado") },
        text = {
            Column {
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID") }
                )
                TextField(
                    value = visitors,
                    onValueChange = { visitors = it },
                    label = { Text("Visitantes (separados por vírgula)") }
                )
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(id, visitors.split(",").map { it.trim() }, notes) }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ExpandableRowItemHousehold(
    household: Households,
    viewModel: HouseholdsViewModel,
    navController: NavController,
    onDetailsClick: (Households) -> Unit,
    onRemoveClick: (Households) -> Unit,
    onEditClick: (Households) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF0F0F0))
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${household.id}",
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )
            }
            if (isExpanded) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // Botão "Detalhes"
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Details",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    onDetailsClick(household)
                                }
                        )
                        Text(
                            text = "Detalhes",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light
                        )
                    }

                    // Botão "Editar"
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    onEditClick(household)
                                }
                        )
                        Text(
                            text = "Editar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light
                        )
                    }

                    // Botão "Apagar"
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    onRemoveClick(household)
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
    }
}
