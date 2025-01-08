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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.domain.model.Visitors
import com.example.myapplication.domain.utils.CheckConnectionUtil
import com.example.myapplication.presentation.viewModels.AgregadosDetalhesViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AgregadosDetalhesScreen(navController: NavHostController, householdId: String) {
    val viewModel: AgregadosDetalhesViewModel = viewModel()
    val context = LocalContext.current

    val showAddMemberDialog = remember { mutableStateOf(false) }

    var selectedVisitor by remember { mutableStateOf<Visitors?>(null) }
    var textFieldValueVisitor by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getVisitors()
        viewModel.getHouseholdMembers(householdId)
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
                    text = "Gerir Agregado",
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
                            showAddMemberDialog.value = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Member",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // pop-up para adicionar novo membro
        if (showAddMemberDialog.value) {
            val newName = remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddMemberDialog.value = false },
                title = {
                    Text(
                        text = "Adicionar Membro",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {

                    Column {
                        Text(text = "Escolher membro a adicionar.")
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f).padding(4.dp)) {
                                Text(
                                    text = "Membro: ${selectedVisitor?.name}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expanded = true }
                                        .padding(8.dp)
                                        .background(
                                            Color.LightGray,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    viewModel.listVisitors.forEach { visitor ->
                                        DropdownMenuItem(
                                            onClick = {
                                                selectedVisitor = visitor
                                                textFieldValueVisitor =
                                                    visitor.name
                                                expanded = false
                                            },
                                            text = { Text(visitor.name) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                dismissButton = {
                    Button(onClick = { showAddMemberDialog.value = false }) {
                        Text("Cancelar")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedVisitor != null && CheckConnectionUtil.isConnected(context)) {
                                viewModel.addMember(householdId, selectedVisitor!!.id)
                                showAddMemberDialog.value = false
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                }
            )
        }


        // Lista de membros do agregado
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.listHouseholdMembers) { visitor ->
                ExpandableRowItemHMember(householdId, visitor)
            }
        }
    }
}


@Composable
fun ExpandableRowItemHMember(householdId: String, visitor: Visitors) {
    val viewModel: AgregadosDetalhesViewModel = viewModel()
    val context = LocalContext.current

    val isExpanded = remember { mutableStateOf(false) }

    val showDetailsDialog = remember { mutableStateOf(false) }
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
                    text = visitor.id,
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

            // region Dropdown das opcoes (Ver Mais, Apagar)
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
                                appendBoldLabel("Nr. Contribuinte: ")
                                append(visitor.taxNo.toString())
                            })
                            Text(buildAnnotatedString {
                                appendBoldLabel("Data de Nascimento: ")
                                append(formattedDOB)
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

            // region apagar membro
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
                            text = "Tem certeza de que deseja remover este visitante do agregado familiar?",
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
                                            viewModel.removeHouseholdMember(householdId, visitor.id)
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