package com.example.myapplication.presentation.screens

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.myapplication.domain.model.Users
import com.example.myapplication.presentation.viewModels.GerirVoluntariosViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@SuppressLint("MutableCollectionMutableState")
@Composable
fun GerirVoluntariosScreen(navController: NavHostController) {
    val viewModel: GerirVoluntariosViewModel = viewModel()

    var searchText by remember { mutableStateOf("") }

    var showFilterDialog by remember { mutableStateOf(false) }
    val options = listOf("Pendentes", "Ativos", "Cancelados")  // 0 - Pendentes; 1 - Ativos; 2 - Cancelados
    val selectedOptions = remember { mutableStateOf(mutableSetOf<String>()) }

    var showSortDialog by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf("ID: Crescente") }


    LaunchedEffect(Unit) {
        viewModel.getUsers()
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
                    text = "Gerir Voluntários",
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

        Row (verticalAlignment = Alignment.CenterVertically) {
            // Campo para procura por nome
            TextField(
                value = searchText,
                onValueChange = { text ->
                    searchText = text
                    viewModel.filterUsers(text)
                },
                label = { Text("Pesquisar por nome") },
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Botão para filtrar
            Button(onClick = { showFilterDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Filt.",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

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

        // Lista de Voluntários
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.listUsers) { user ->
                ExpandableRowItemUser(user)
            }
        }

        //region Filtrar
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = {
                    Text(text = "Filtrar Voluntários")
                },
                text = {
                    Column {
                        options.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val updatedSet = selectedOptions.value.toMutableSet()
                                        if (updatedSet.contains(option)) {
                                            updatedSet.remove(option)
                                        } else {
                                            updatedSet.add(option)
                                        }
                                        selectedOptions.value = updatedSet
                                    }
                            ) {
                                Checkbox(
                                    checked = selectedOptions.value.contains(option),
                                    onCheckedChange = { isChecked ->
                                        val updatedSet = selectedOptions.value.toMutableSet()
                                        if (isChecked) {
                                            updatedSet.add(option)
                                        } else {
                                            updatedSet.remove(option)
                                        }
                                        selectedOptions.value = updatedSet
                                    }
                                )
                                Text(text = option, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.filterUsersState(selectedOptions.value)
                            showFilterDialog = false
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showFilterDialog = false }) {
                        Text("Fechar")
                    }
                }
            )
        }
        //endregion

        //region Ordenar
        if (showSortDialog) {
            AlertDialog(
                onDismissRequest = { showSortDialog = false },
                title = {
                    Text(text = "Ordenar Voluntários")
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
                            viewModel.sortUsers(selectedSortOption)
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
private fun ExpandableRowItemUser(user: Users) {
    val viewModel: GerirVoluntariosViewModel = viewModel()
    val firebaseUser = Firebase.auth.currentUser

    val isExpanded = remember { mutableStateOf(false) }

    val showDetailsDialog = remember { mutableStateOf(false) }
    val showActivateDialog = remember { mutableStateOf(false) }
    val showCancelDialog = remember { mutableStateOf(false) }

    val countryName = remember { mutableStateOf("A Carregar...") }

    // Procura o nome de um pais
    LaunchedEffect(user.countriesId) {
        viewModel.fetchCountryName(user.countriesId) { name ->
            countryName.value = name ?: "Desconhecido"
        }
    }


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
                    text = user.id,
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
                    text = user.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.7f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                when(user.state) {
                    0 -> StateIcon(Icons.Filled.Schedule)
                    1 -> StateIcon(Icons.Filled.CheckCircleOutline)
                    2 -> StateIcon(Icons.Filled.HighlightOff)
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { isExpanded.value = !isExpanded.value }
                )
            }

            // region Dropdown das opcoes (Ver Mais, Ativar, Cancelar)
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
                            //Ativar
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Activate",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        showActivateDialog.value = true
                                    }
                            )
                            Text(
                                text = "Ativar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Cancelar
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        showCancelDialog.value = true
                                    }
                            )
                            Text(
                                text = "Cancelar",
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
                            text = "Detalhes do Voluntário",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column {
                            Text(buildAnnotatedString {
                                appendBoldLabel("Nome: ")
                                append(user.name)
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Data de Nascimento: ")
                                append(user.dob)
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Cidade: ")
                                append(user.city)
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("País: ")
                                append(countryName.value)
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Contacto: ")
                                append(user.phoneNo.toString())
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

            //region Ativar
            if (showActivateDialog.value) {
                AlertDialog(
                    onDismissRequest = { showActivateDialog.value = false },
                    title = {
                        Text(
                            text = "Ativar Voluntário",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Text(
                            text = "Tem certeza de que deseja ativar este voluntário?",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showActivateDialog.value = false
                            }
                        ) {
                            Text("Cancelar")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val updatedUser = user.copy(
                                    state = 1
                                )
                                viewModel.updateUser(user.id, updatedUser, firebaseUser!!.uid)
                                showActivateDialog.value = false
                            }
                        )
                        {
                            Text("Confirmar")
                        }
                    }

                )
            }
            //endregion

            // region Cancelar
            if (showCancelDialog.value) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog.value = false },
                    title = {
                        Text(
                            text = "Cancelar Voluntário",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Text(
                            text = "Tem certeza de que deseja cancelar este voluntário?",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showCancelDialog.value = false
                            }
                        ) {
                            Text("Cancelar")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val updatedUser = user.copy(
                                    admin = false,
                                    state = 2
                                )
                                viewModel.updateUser(user.id, updatedUser, firebaseUser!!.uid)
                                showCancelDialog.value = false
                            }
                        )
                        {
                            Text("Confirmar")
                        }
                    }

                )
            }
            //endregion
        }
    }
}

@Composable
fun StateIcon(icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = "State Icon",
        modifier = Modifier.size(24.dp)
    )

}

private fun AnnotatedString.Builder.appendBoldLabel(label: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(label)
    }
}