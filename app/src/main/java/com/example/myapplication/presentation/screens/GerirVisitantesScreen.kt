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
import com.example.myapplication.domain.model.Visitors
import com.example.myapplication.presentation.viewModels.GerirVisitantesViewModel
import com.google.firebase.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GerirVisitantesScreen(navController: NavHostController) {
    val showNewVisitorDialog = remember { mutableStateOf(false) }
    val viewModel: GerirVisitantesViewModel = viewModel()

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
                    text = "Gerir Visitantes",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
                // Icon "+" para adicionar visitante
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(1.dp, Color.Black, shape = CircleShape)
                        .clickable {
                            showNewVisitorDialog.value = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Visit",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // pop-up para adicionar novo visitante
        if (showNewVisitorDialog.value) {
            val newName = remember { mutableStateOf("") }
            val newTaxNo = remember { mutableStateOf("") }
            val newDOB = remember { mutableStateOf("") }
            val newCountry = remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showNewVisitorDialog.value = false },
                title = {
                    Text(
                        text = "Add New Visitor",
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
                            value = newTaxNo.value,
                            onValueChange = { newTaxNo.value = it },
                            label = { Text("Contacto") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        TextField(
                            value = newDOB.value,
                            onValueChange = { newDOB.value = it },
                            label = { Text("Data de Nascimento (dd/MM/aaaa)") }
                        )
                        TextField(
                            value = newCountry.value,
                            onValueChange = { newCountry.value = it },
                            label = { Text("País de Origem") }
                        )
                    }
                },
                dismissButton = {
                    Button(onClick = { showNewVisitorDialog.value = false }) {
                        Text("Cancel")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newVisitor = Visitors(
                                id = "",
                                name = newName.value,
                                taxNo = newTaxNo.value.toIntOrNull() ?: 0,
                                dob = parseDate(newDOB.value)?.let { Timestamp(it) } ?: Timestamp.now(),
                                countriesId = newCountry.value
                            )
                            viewModel.addVisitor(newVisitor)
                            showNewVisitorDialog.value = false
                        }
                    ) {
                        Text("Confirm")
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
                    viewModel.filterVisitors(text) // Filter the list as text changes
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

        // Lista de Visitantes
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.listVisitors) { visitor ->
                ExpandableRowItem(visitor)
            }
        }

        //region Ordenar
        if (showSortDialog) {
            AlertDialog(
                onDismissRequest = { showSortDialog = false },
                title = {
                    Text(text = "Ordenar Visitantes")
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
                            viewModel.sortVisitors(selectedSortOption)
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
fun ExpandableRowItem(visitor: Visitors) {
    val viewModel: GerirVisitantesViewModel = viewModel()

    val isExpanded = remember { mutableStateOf(false) }

    val showDetailsDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }

    val countryName = remember { mutableStateOf("A Carregar...") }

    // Procura o nome de um pais
    LaunchedEffect(visitor.countriesId) {
        viewModel.fetchCountryName(visitor.countriesId) { name ->
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
                    text = visitor.id,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.3f)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp) // Thickness of the line
                        .height(20.dp) // Adjust the height as needed
                        .background(Color.Gray) // Line color
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = visitor.name,
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
                val formattedDOB = visitor.dob.toDate().let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                }

                AlertDialog(
                    onDismissRequest = { showDetailsDialog.value = false },
                    title = {
                        Text(
                            text = "Detalhes do Visitante",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Column {
                            Text(buildAnnotatedString {
                                appendBoldLabel("Nome: ")
                                append(visitor.name)
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Contacto: ")
                                append(visitor.taxNo.toString())
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Data de Nascimento: ")
                                append(formattedDOB)
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Pais de Origem: ")
                                append(countryName.value)
                            })

                            // !!! Ver se vale a pena meter o agregado familiar
                            /*
                            if (householdMembers.value.isNotEmpty()) {
                                Text(
                                    text = "Agregado Familiar:",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                                ) {
                                    Column {
                                        // Table headers
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFFE0E0E0))
                                                .border(1.dp, Color.Black),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Contacto",
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(8.dp),
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = "Nome",
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(8.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        // Table rows
                                        householdMembers.value.forEach { (taxNo, name) ->
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .border(1.dp, Color.Black),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = taxNo,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(8.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    text = name,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(8.dp),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            */
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
                val updatedName = remember { mutableStateOf(visitor.name) }
                val updatedTaxNo = remember { mutableStateOf(visitor.taxNo.toString()) }
                val updatedDOB = remember { mutableStateOf(visitor.dob.toDate().let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                }) }
                val updatedCountry = remember { mutableStateOf(countryName.value) }
                AlertDialog(
                    onDismissRequest = { showEditDialog.value = false },
                    title = {
                        Text(
                            text = "Editar Visita",
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
                                value = updatedTaxNo.value,
                                onValueChange = { updatedTaxNo.value = it },
                                label = { Text("Contacto") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            TextField(
                                value = updatedDOB.value,
                                onValueChange = { updatedDOB.value = it },
                                label = { Text("Data de Nascimento (dd/MM/aaaa)") }
                            )
                            TextField(
                                value = updatedCountry.value,
                                onValueChange = { updatedCountry.value = it },
                                label = { Text("Pais de Origem") }
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
                                val updatedVisitor = visitor.copy(
                                    name = updatedName.value,
                                    taxNo = updatedTaxNo.value.toIntOrNull() ?: visitor.taxNo,
                                    dob = parseDate(updatedDOB.value)?.let { Timestamp(it) } ?: visitor.dob,
                                    countriesId = updatedCountry.value
                                )
                                viewModel.updateVisitor(visitor.id, updatedVisitor)
                                showEditDialog.value = false
                            }
                        )
                        {
                            Text("Confirmar")
                        }
                    }

                )
            }
            //endregion

            // !!! Trocar para desativar visitante ou apagar tudo aonde este visitante aparece
            // region apagar visita
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
                            text = "Tem certeza de que deseja apagar este visitante?",
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
                                        viewModel.deleteVisitor(visitor.id)
                                        showDeleteDialog.value = false
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

fun parseDate(dateString: String): Date? {
    return try {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateString)
    } catch (e: ParseException) {
        null
    }
}

private fun AnnotatedString.Builder.appendBoldLabel(label: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(label)
    }
}