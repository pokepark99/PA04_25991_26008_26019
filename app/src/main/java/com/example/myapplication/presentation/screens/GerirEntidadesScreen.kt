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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.domain.model.Entities
import com.example.myapplication.domain.utils.CheckConnectionUtil
import com.example.myapplication.presentation.viewModels.GerirEntidadesViewModel

@Composable
fun GerirEntidadesScreen(navController: NavHostController) {
    val showNewEntityDialog = remember { mutableStateOf(false) }
    val viewModel: GerirEntidadesViewModel = viewModel()
    val context = LocalContext.current

    var searchText by remember { mutableStateOf("") }

    var showSortDialog by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf("ID: Crescente") }

    LaunchedEffect(Unit) {
        viewModel.getVisitors()
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
                    text = "Gerir Entidades",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
                // Icon "+" para adicionar entidade
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(1.dp, Color.Black, shape = CircleShape)
                        .clickable {
                            showNewEntityDialog.value = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Entity",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // pop-up para adicionar nova entidade
        if (showNewEntityDialog.value) {
            val newName = remember { mutableStateOf("") }
            val newAddress = remember { mutableStateOf("") }
            val newEmail = remember { mutableStateOf("") }
            val newPhoneNo = remember { mutableStateOf("") }
            val newNotes = remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showNewEntityDialog.value = false },
                title = {
                    Text(
                        text = "Adicionar Nova Entidade",
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
                        TextField(
                            value = newAddress.value,
                            onValueChange = { newAddress.value = it },
                            label = { Text("Morada") }
                        )
                        TextField(
                            value = newEmail.value,
                            onValueChange = { newEmail.value = it },
                            label = { Text("Email") }
                        )
                        TextField(
                            value = newPhoneNo.value,
                            onValueChange = { newPhoneNo.value = it },
                            label = { Text("Contacto") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        TextField(
                            value = newNotes.value,
                            onValueChange = { newNotes.value = it },
                            label = { Text("Notas") }
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = { showNewEntityDialog.value = false }) {
                        Text("Cancelar")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newEntity = Entities(
                                id = "",
                                name = newName.value,
                                address = newAddress.value,
                                email = newEmail.value,
                                phoneNo = newPhoneNo.value.toIntOrNull() ?: 0,
                                notes = newNotes.value
                            )
                            if(CheckConnectionUtil.isConnected(context)) {
                                viewModel.addEntity(newEntity)
                                showNewEntityDialog.value = false
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
                    viewModel.filterEntities(text)
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

        // Lista de Entidades
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.listEntities) { entity ->
                ExpandableRowItemEntity(entity)
            }
        }

        //region Ordenar
        if (showSortDialog) {
            AlertDialog(
                onDismissRequest = { showSortDialog = false },
                title = {
                    Text(text = "Ordenar Entidades")
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
                            viewModel.sortEntities(selectedSortOption)
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
private fun ExpandableRowItemEntity(entity: Entities) {
    val viewModel: GerirEntidadesViewModel = viewModel()
    val context = LocalContext.current

    val isExpanded = remember { mutableStateOf(false) }

    val showDetailsDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
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
                    text = entity.id,
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
                    text = entity.name,
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

            // region Dropdown das opcoes (Ver Mais, Editar, Apagar)
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
                            //Ver Mais
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "See More",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        showDetailsDialog.value = true
                                    }
                            )
                            Text(
                                text = "Ver Mais",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Editar
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        showEditDialog.value = true
                                    }
                            )
                            Text(
                                text = "Editar",
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


            //region Ver Mais
            if (showDetailsDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDetailsDialog.value = false },
                    title = {
                        Text(
                            text = "Detalhes da Entidade",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column {
                            Text(buildAnnotatedString {
                                appendBoldLabel("Nome: ")
                                append(entity.name)
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Morada: ")
                                append(entity.address)
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Email: ")
                                append(entity.email)
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Contacto: ")
                                append(entity.phoneNo.toString())
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Notas: ")
                                append(entity.notes)
                            })
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showDetailsDialog.value = false }) {
                            Text("Fechar")
                        }
                    }
                )
            }
            //endregion

            //region Editar
            if (showEditDialog.value) {
                val updatedName = remember { mutableStateOf(entity.name) }
                val updatedAddress = remember { mutableStateOf(entity.address) }
                val updatedEmail = remember { mutableStateOf(entity.email) }
                val updatedPhoneNo = remember { mutableStateOf(entity.phoneNo.toString()) }
                val updatedNotes = remember { mutableStateOf(entity.notes) }

                AlertDialog(
                    onDismissRequest = { showEditDialog.value = false },
                    title = {
                        Text(
                            text = "Editar Entidade",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column {
                            TextField(
                                value = updatedName.value,
                                onValueChange = { updatedName.value = it },
                                label = { Text("Nome") }
                            )
                            TextField(
                                value = updatedAddress.value,
                                onValueChange = { updatedAddress.value = it },
                                label = { Text("Morada") }
                            )
                            TextField(
                                value = updatedEmail.value,
                                onValueChange = { updatedEmail.value = it },
                                label = { Text("Email") }
                            )
                            TextField(
                                value = updatedPhoneNo.value,
                                onValueChange = { updatedPhoneNo.value = it },
                                label = { Text("Contacto") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            TextField(
                                value = updatedNotes.value,
                                onValueChange = { updatedNotes.value = it },
                                label = { Text("Notas") }
                            )
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showEditDialog.value = false
                            }
                        ) {
                            Text("Cancelar")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val updatedEntity = entity.copy(
                                    name = updatedName.value,
                                    address = updatedAddress.value,
                                    email = updatedEmail.value,
                                    phoneNo = updatedPhoneNo.value.toIntOrNull() ?: entity.phoneNo,
                                    notes = updatedNotes.value
                                )
                                if(CheckConnectionUtil.isConnected(context)) {
                                    viewModel.updateEntity(entity.id, updatedEntity)
                                    showEditDialog.value = false
                                }
                            }
                        )
                        {
                            Text("Confirmar")
                        }
                    }

                )
            }
            //endregion

            // !!! Trocar para desativar entidade?
            // region apagar entidade
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
                            text = "Tem certeza de que deseja apagar esta entidade?",
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
                                            viewModel.deleteEntity(entity.id)
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

private fun AnnotatedString.Builder.appendBoldLabel(label: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(label)
    }
}