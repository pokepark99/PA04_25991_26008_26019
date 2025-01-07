package com.example.myapplication.presentation.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.myapplication.domain.model.EntriesDetail
import com.example.myapplication.domain.utils.CheckConnectionUtil
import com.example.myapplication.presentation.viewModels.CandidaturaHorarioViewModel

//region Screen dos Voluntários
@Composable
fun CandidaturaHorarioScreen(navController: NavHostController) {
    val viewModel: CandidaturaHorarioViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.getCandidaturas()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopSection(navController)

        // Lista de Candidaturas
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.listEntriesDetail) { entry ->
                if (entry.state == 0){
                    RowCandidaturaOptionExpandable(viewModel, entry, false)
                } else {
                    RowCandidaturaOption(entry, false)
                }
            }
        }
    }
}
//endregion

//region Screen dos Gestores
@Composable
fun CandidaturaHorarioScreen(navController: NavHostController, scheduleId: String) {
    val viewModel: CandidaturaHorarioViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.getCandidaturasHorario(scheduleId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopSection(navController)

        // Lista de Candidaturas
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.listEntriesDetail) { entry ->
                if (entry.state == 0){
                    RowCandidaturaOptionExpandable(viewModel, entry, true)
                } else {
                    RowCandidaturaOption(entry, true)
                }
            }
        }
    }
}
//endregion

@Composable
fun TopSection(navController: NavHostController) {Box(
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
            text = "Ver Candidaturas",
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
}

@Composable
fun RowCandidaturaOptionExpandable(viewModel: CandidaturaHorarioViewModel, entry: EntriesDetail, isGestor: Boolean) {
    val isExpanded = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val showAcceptDialog = remember { mutableStateOf(false) }
    val showCancelDialog = remember { mutableStateOf(false) }

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
                    text = entry.positionName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.4f)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))

                if (isGestor) {
                    Text(
                        text = entry.userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(0.6f)
                    )
                } else {
                    Text(
                        text = entry.scheduleDate,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(0.6f)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                StateIcon(Icons.Filled.Schedule)

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { isExpanded.value = !isExpanded.value }
                )
            }

            // region Dropdown das opcoes
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
                        //Aceitar
                        if (isGestor) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Accept",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable {
                                            showAcceptDialog.value = true
                                        }
                                )
                                Text(
                                    text = "Aceitar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
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

            // region Aceitar
            if (showAcceptDialog.value) {
                AlertDialog(
                    onDismissRequest = { showAcceptDialog.value = false },
                    title = {
                        Text(
                            text = "Aceitar Candidatura",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Text(
                            text = "Tem certeza de que deseja aceitar esta candidatura?",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showAcceptDialog.value = false
                            }
                        ) {
                            Text("Cancelar")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val updatedEntry = entry.copy(
                                    state = 1
                                )
                                if(CheckConnectionUtil.isConnected(context)) {
                                    viewModel.updateCandidaturaHorario(updatedEntry)
                                    showAcceptDialog.value = false
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

            // region Cancelar
            if (showCancelDialog.value) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog.value = false },
                    title = {
                        Text(
                            text = "Cancelar Candidatura",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    text = {
                        Text(
                            text = "Tem certeza de que deseja cancelar esta candidatura?",
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
                                val updatedEntry = entry.copy(
                                    state = 2
                                )
                                if(CheckConnectionUtil.isConnected(context)) {
                                    viewModel.updateCandidaturaHorario(updatedEntry)
                                    showCancelDialog.value = false
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
        }
    }
}

@Composable
fun RowCandidaturaOption(entry: EntriesDetail, isGestor: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = entry.positionName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(0.4f)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(8.dp))

                if (isGestor) {
                    Text(
                        text = entry.userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(0.6f)
                    )
                } else {
                    Text(
                        text = entry.scheduleDate,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(0.6f)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                when(entry.state) {
                    1 -> StateIcon(Icons.Filled.CheckCircleOutline)
                    2 -> StateIcon(Icons.Filled.HighlightOff)
                }
            }
        }
    }
}