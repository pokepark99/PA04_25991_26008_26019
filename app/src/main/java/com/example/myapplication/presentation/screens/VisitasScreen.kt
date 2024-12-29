package com.example.myapplication.presentation.screens

import android.app.TimePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.myapplication.domain.model.Schedules
import com.example.myapplication.domain.model.Visitors
import com.example.myapplication.domain.model.Visits
import com.example.myapplication.presentation.viewModels.VisitasViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@Composable
fun VisitasScreen(navController: NavHostController, storeId: String?){
    //region variaveis
    val context = LocalContext.current
    val viewModel: VisitasViewModel = viewModel()

    // Pesquisa de visitantes
    val searchVisitor = remember { mutableStateOf("") }
    val visitorResults = remember { mutableStateOf<List<Visitors>>(emptyList()) }
    val selectedVisitor = remember { mutableStateOf<Visitors?>(null) }

    //para novo visitante
    val name = remember { mutableStateOf("") }
    val taxNo = remember { mutableStateOf("") }
    val country = remember { mutableStateOf("") }

    //selecionar DOB
    val selectedDay = remember { mutableStateOf("") }
    val selectedMonth = remember { mutableStateOf("") }
    val selectedYear = remember { mutableStateOf("") }
    val days = (1..31).map { it.toString() }
    val months = listOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
    val years = (1930..2024).map { it.toString() }
    val dayDropdownExpanded = remember { mutableStateOf(false) }
    val monthDropdownExpanded = remember { mutableStateOf(false) }
    val yearDropdownExpanded = remember { mutableStateOf(false) }

    val schedules = viewModel.schedules.collectAsState().value
    val visitsWithVisitors = remember { mutableStateOf<List<Pair<Visits, Visitors>>>(emptyList()) }

    //pop-up para adicionar visita
    val showAdicionar = remember { mutableStateOf(false) }
    val showNewVisitorDialog = remember { mutableStateOf(false) }

    //schedule dropdown
    val selectedSchedule = remember { mutableStateOf<Schedules?>(null)}

    //endregion

    //busca todos os horarios para a loja
    if (storeId != null) {
        viewModel.fetchSchedules(storeId)
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
                // Titlo
                Text(
                    text = "Visitas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
                // Icon "+" para adicionar Visita
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(1.dp, Color.Black, shape = CircleShape)
                        .clickable {
                            showAdicionar.value = true
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
        // Adicionar visita
        if (showAdicionar.value) {
            AlertDialog(
                onDismissRequest = { showAdicionar.value = false },
                title = { Text("Adicionar Visita") },
                text = {
                    Column {
                        //presquisar visitante
                        Text("Pesquisar Visitante")
                        TextField(
                            value = searchVisitor.value,
                            onValueChange = { search ->
                                searchVisitor.value = search
                                viewModel.searchVisitors(search) { results ->
                                    visitorResults.value = results
                                }
                            },
                            placeholder = { Text("Nome ou Contacto") }
                        )
                        LazyColumn(modifier = Modifier.fillMaxHeight(0.5f)) {
                            items(visitorResults.value) { visitor ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedVisitor.value = visitor }
                                        .background(if (selectedVisitor.value == visitor) Color.LightGray else Color.Transparent),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = visitor.name,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .weight(1f)
                                    )
                                    Text(
                                        text = "Cont.: ${visitor.taxNo}",
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                        //adicionar novo visitante
                        Button(
                            onClick = {
                                showNewVisitorDialog.value = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Novo Visitante")
                        }

                        // region pop-up Novo Visitante
                        // pop-up para adicionar novo visitante
                        if (showNewVisitorDialog.value) {
                            AlertDialog(
                                onDismissRequest = { showNewVisitorDialog.value = false },
                                title = { Text("Novo Visitante") },
                                text = {
                                    Column {
                                        TextField(
                                            value = name.value,
                                            onValueChange = { name.value = it },
                                            label = { Text("Nome") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        )
                                        // Dropdowns para dia, mes e ano
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            // Dia Dropdown
                                            Box(modifier = Modifier.weight(1f).padding(4.dp)) {
                                                Text(
                                                    text = "Dia: ${selectedDay.value}",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { dayDropdownExpanded.value = true }
                                                        .padding(8.dp)
                                                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                                                )
                                                DropdownMenu(
                                                    expanded = dayDropdownExpanded.value,
                                                    onDismissRequest = { dayDropdownExpanded.value = false },
                                                    modifier = Modifier.width(120.dp)
                                                ) {
                                                    days.forEach { day ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                selectedDay.value = day
                                                                dayDropdownExpanded.value = false
                                                            },
                                                            text = { Text(day) }
                                                        )
                                                    }
                                                }
                                            }
                                            // Mes Dropdown
                                            Box(modifier = Modifier.weight(1f).padding(4.dp)) {
                                                Text(
                                                    text = "Mês: ${selectedMonth.value}",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { monthDropdownExpanded.value = true }
                                                        .padding(8.dp)
                                                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                                                )
                                                DropdownMenu(
                                                    expanded = monthDropdownExpanded.value,
                                                    onDismissRequest = { monthDropdownExpanded.value = false },
                                                    modifier = Modifier.width(120.dp)
                                                ) {
                                                    months.forEachIndexed { index, month ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                selectedMonth.value = (index + 1).toString()
                                                                monthDropdownExpanded.value = false
                                                            },
                                                            text = { Text(month) }
                                                        )
                                                    }
                                                }
                                            }
                                            // Ano Dropdown
                                            Box(modifier = Modifier.weight(1.5f).padding(4.dp)) {
                                                Text(
                                                    text = "Ano:${selectedYear.value}",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            yearDropdownExpanded.value = true
                                                        }
                                                        .padding(8.dp)
                                                        .background(
                                                            Color.LightGray,
                                                            shape = RoundedCornerShape(4.dp)
                                                        )
                                                )
                                                DropdownMenu(
                                                    expanded = yearDropdownExpanded.value,
                                                    onDismissRequest = {
                                                        yearDropdownExpanded.value = false
                                                    },
                                                    modifier = Modifier.width(120.dp)
                                                ) {
                                                    years.forEach { year ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                selectedYear.value = year
                                                                yearDropdownExpanded.value = false
                                                            },
                                                            text = { Text(year) }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        // Textfiels para contacto e Pais
                                        TextField(
                                            value = taxNo.value,
                                            onValueChange = { taxNo.value = it },
                                            label = { Text("Número de Contacto") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )

                                        TextField(
                                            value = country.value,
                                            onValueChange = { country.value = it },
                                            label = { Text("País") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        )
                                    }
                                },
                                //confirmar novo visitante
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            val nameText = name.value.trim()
                                            val taxNoValue = taxNo.value.toIntOrNull()
                                            val countryText = country.value.trim()

                                            val dobTimestamp = try {
                                                val day = selectedDay.value.toIntOrNull()
                                                val month = selectedMonth.value.toIntOrNull()
                                                val year = selectedYear.value.toIntOrNull()

                                                if (day != null && month != null && year != null) {
                                                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

                                                    calendar.set(Calendar.DAY_OF_MONTH, day)
                                                    calendar.set(Calendar.MONTH, month - 1)
                                                    calendar.set(Calendar.YEAR, year)
                                                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                                                    calendar.set(Calendar.MINUTE, 0)
                                                    calendar.set(Calendar.SECOND, 0)
                                                    calendar.set(Calendar.MILLISECOND, 0)

                                                    val date = calendar.time

                                                    // converter date para Firebase Timestamp
                                                    Timestamp(date)
                                                } else {
                                                    null
                                                }
                                            } catch (e: Exception) {
                                                null
                                            }
                                            if (nameText.isNotEmpty() && dobTimestamp != null && taxNoValue != null && countryText.isNotEmpty()) {
                                                viewModel.addVisitor(nameText, dobTimestamp, taxNoValue, countryText) { success ->
                                                    if (success) {
                                                        Toast.makeText(context, "Visitante adicionado com sucesso", Toast.LENGTH_SHORT).show()
                                                        showNewVisitorDialog.value = false
                                                        name.value = ""
                                                        selectedDay.value = ""
                                                        selectedMonth.value = ""
                                                        selectedYear.value = ""
                                                        taxNo.value = ""
                                                        country.value = ""
                                                    } else {
                                                        Toast.makeText(context, "Erro ao adicionar visitante", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Text("Adicionar")
                                    }
                                },
                                // botao para cancelar adicionar novo visitante
                                dismissButton = {
                                    Button(onClick = { showNewVisitorDialog.value = false }) {
                                        Text("Cancelar")
                                    }
                                }
                            )
                        }
                        // endregion
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedVisitor.value?.let { visitor ->
                                viewModel.addVisit(visitorId = visitor.id, storeId = storeId) { success ->
                                    if (success) {
                                        showAdicionar.value = false
                                        selectedVisitor.value = null
                                    } else {
                                        Log.e("VisitasScreen", "Failed to add visit")
                                    }
                                }
                            }
                        },
                        enabled = selectedVisitor.value != null
                    ) {
                        Text("Adicionar")
                    }
                },
                dismissButton = {
                    //botao para cancelar nova visita
                    Button(onClick = { showAdicionar.value = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Dropdown para horarios
        if (schedules.isNotEmpty()) {
            if (selectedSchedule.value == null) {
                selectedSchedule.value = schedules.firstOrNull() // Horario mais recente como default
                selectedSchedule.value?.let { schedule ->
                    if (storeId != null) {
                        viewModel.fetchVisitsForSchedule(storeId, schedule) { result ->
                            visitsWithVisitors.value = result
                        }
                    }
                }
            }
            DropdownMenuUI(
                schedules = schedules,
                selectedSchedule = selectedSchedule.value,
                onScheduleSelected = { schedule ->
                    selectedSchedule.value = schedule
                    if (storeId != null) {
                        viewModel.fetchVisitsForSchedule(storeId, schedule) { result ->
                            visitsWithVisitors.value = result
                        }
                    }
                }
            )
        }

        // Lista de Visitas para o horario
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(visitsWithVisitors.value) { (visit, visitor) ->
                VisitItemWithVisitor(visit, visitor)
            }
        }
    }
}

//dropdown de horarios
@Composable
fun DropdownMenuUI(
    schedules: List<Schedules>,
    selectedSchedule: Schedules?,
    onScheduleSelected: (Schedules) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .clickable { expanded.value = true }
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = selectedSchedule?.let {
                    val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("pt", "PT"))
                    dateFormat.format(it.dateStart.toDate())
                } ?: "Select a Schedule",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown Arrow")
        }

        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            schedules.forEach { schedule ->
                DropdownMenuItem(
                    text = {
                        val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("pt", "PT"))
                        Text(dateFormat.format(schedule.dateStart.toDate()))
                    },
                    onClick = {
                        onScheduleSelected(schedule)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

//visitas
@Composable
fun VisitItemWithVisitor(visit: Visits, visitor: Visitors) {
    //region variaveis
    val viewModel: VisitasViewModel = viewModel()
    val context = LocalContext.current

    val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "PT"))
    val formattedTime = timeFormat.format(visit.date.toDate())

    // ver opcoes (Ver Mais, Editar, Apagar)
    val isExpanded = remember { mutableStateOf(false) }

    //Ver Mais
    val showDetailsDialog = remember { mutableStateOf(false) }
    val householdMembers = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    //Editar
    val showEditDialog = remember { mutableStateOf(false) }
    //amostra pop up para eliminar visita
    val showDeleteDialog = remember { mutableStateOf(false) }

    val countryName = remember { mutableStateOf("Carregando...") }
    //endregion

    // Busca o nome de um pais
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
            .clickable {isExpanded.value = !isExpanded.value}
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$formattedTime ${visitor.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.weight(1f)
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
                                        // busca dados do agregado familiar
                                        viewModel.fetchHouseholdDetails(visitor.id) { family ->
                                            householdMembers.value = family
                                        }
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
            //endregion
        }
    }

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
        val updatedTime = remember { mutableStateOf(visit.date.toDate()) }
        val updatedVisitor = remember { mutableStateOf(visitor) }
        val expanded = remember { mutableStateOf(false) } //dropdown dos visitantes
        val visitorsList = remember { mutableStateOf<List<Visitors>>(emptyList()) }

        // todos os visitantes
        LaunchedEffect(Unit) {
            viewModel.fetchAllVisitors { fetchedVisitors ->
                visitorsList.value = fetchedVisitors
            }
        }

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
                    // Time Picker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Hora:",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        val calendar = Calendar.getInstance()
                        calendar.time = updatedTime.value

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}",
                                modifier = Modifier
                                    .clickable {
                                        TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                calendar.set(Calendar.MINUTE, minute)
                                                updatedTime.value = calendar.time
                                            },
                                            calendar.get(Calendar.HOUR_OF_DAY),
                                            calendar.get(Calendar.MINUTE),
                                            true
                                        ).show()
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                    // Visitor Dropdown
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Visitante:",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
                                .clickable { expanded.value = true }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = updatedVisitor.value.name,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                            visitorsList.value.forEach { availableVisitor ->
                                DropdownMenuItem(
                                    text = { Text(availableVisitor.name) },
                                    onClick = {
                                        updatedVisitor.value = availableVisitor
                                        expanded.value = false
                                    }
                                )
                            }
                        }
                    }
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
                        val updatedFields = mapOf(
                        "Date" to Timestamp(updatedTime.value),
                        "VisitorsId" to updatedVisitor.value.id
                        )
                        viewModel.updateVisit(
                            visit.id,
                            updatedFields
                        )
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
                    text = "Tem certeza de que deseja apagar esta visita?",
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
                                viewModel.deleteVisit(visit.id)
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

private fun AnnotatedString.Builder.appendBoldLabel(label: String) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(label)
    }
}
