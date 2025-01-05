package com.example.myapplication.presentation.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ForwardToInbox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.myapplication.domain.model.Positions
import com.example.myapplication.domain.model.Schedules
import com.example.myapplication.presentation.viewModels.HorariosViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun HorariosScreen(navController: NavHostController, isGestor: Boolean){
    //region variaveis
    val viewModel: HorariosViewModel = viewModel()
    val context = LocalContext.current

    // adicionar horario
    val showAdicionar = remember { mutableStateOf(false) }
    val stores = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    val selectedStore = remember { mutableStateOf<String?>(null) }
    val date = remember { mutableStateOf("") }
    val startTime = remember { mutableStateOf("") }
    val endTime = remember { mutableStateOf("") }
    val isDropdownExpanded = remember { mutableStateOf(false) } // dropdown de lojas
    val isOpenExpanded = remember { mutableStateOf(false) } // dropdown sim/nao
    val isOpenSelected = remember { mutableStateOf<Boolean?>(null) }
    //horarios
    val schedules = remember { mutableStateOf<List<Schedules>>(emptyList()) }
    //endregion

    // fetch de horarios e lojas
    LaunchedEffect(Unit){
        viewModel.fetchSchedules { fetchedSchedules ->
            schedules.value = fetchedSchedules
        }
        viewModel.fetchStores { fetchedStores ->
            stores.value = fetchedStores
        }
    }

    //region Main
    Column (
        modifier = Modifier.fillMaxSize()
    ){
        Box (
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
                    text = "Horários",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
                if(isGestor){
                    // Icon "+" para adicionar Horário
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
                            contentDescription = "Add Schedule",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    // Icon para ver candidaturas a Horários
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Color.Black, shape = CircleShape)
                            .clickable {
                                navController.navigate("candidatura_horario")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ForwardToInbox,
                            contentDescription = "View Entries",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                }
            }
        }
        LazyColumn (
            modifier = Modifier.fillMaxHeight()
        ){
            item {
                //horarios abertos
                ScheduleSection(
                    navController,
                    title = "Horários Abertos",
                    schedules = schedules.value.filter { it.open }
                        .sortedBy { it.dateStart.toDate() },
                    stores = stores.value,
                    isGestor
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                //horarios fechados
                val (futureSchedules, pastSchedules) = schedules.value.filter { !it.open }
                    .partition { it.dateStart.toDate().after(Calendar.getInstance().time) } // dividir em pasado e futuro

                // ordenar horarios futuros (mais cedo primeiro)
                val sortedFutureSchedules = futureSchedules.sortedBy { it.dateStart.toDate() }

                // ordenar horarios passados (mais recente primeiro)
                val sortedPastSchedules = pastSchedules.sortedByDescending { it.dateStart.toDate() }

                ScheduleSection(
                    navController,
                    title = "Horários Fechados",
                    schedules = sortedFutureSchedules + sortedPastSchedules, // combinar futuro e pasado
                    stores = stores.value,
                    isGestor
                )
            }

        }
    }
    // region Adicionar Horarios
    if (showAdicionar.value) {
        //pop-up para adicionar horario
        AlertDialog(
            onDismissRequest = { showAdicionar.value = false },
            title = { Text("Novo Horário") },
            text = {
                Column {
                    // Escolher data
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text("Data:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), shape = CircleShape)
                                .clickable {
                                    val calendar = Calendar.getInstance()
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            calendar.set(year, month, dayOfMonth)
                                            date.value = SimpleDateFormat(
                                                "dd/MM/yyyy",
                                                Locale.getDefault()
                                            ).format(calendar.time)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = date.value.ifEmpty { "Selecionar Data" },
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                    // tempo de inicio e de fim do horario
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text("Hora:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        // inicio
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), shape = CircleShape)
                                .clickable {
                                    val calendar = Calendar.getInstance()
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            startTime.value = String.format("%02d:%02d", hour, minute)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                }
                                .padding(8.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = startTime.value.ifEmpty { "Início" },
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                        Text("-", modifier = Modifier.padding(horizontal = 8.dp))
                        // fim
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), shape = CircleShape)
                                .clickable {
                                    val calendar = Calendar.getInstance()
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            endTime.value = String.format("%02d:%02d", hour, minute)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                }
                                .padding(8.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = endTime.value.ifEmpty { "Fim" },
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                    // Dropdown da loja
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Loja:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), shape = CircleShape)
                                .clickable {
                                    isDropdownExpanded.value = true
                                }
                        ) {
                            Text(
                                text = selectedStore.value?.let { storeId ->
                                    stores.value.find { it.first == storeId }?.second ?: "Selecionar Loja"
                                } ?: "Selecionar Loja",
                                modifier = Modifier
                                    .background(Color(0xFFF0F0F0))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                color = Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded.value,
                            onDismissRequest = {isDropdownExpanded.value = false},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            stores.value.forEach { (storeId, storeName) ->
                                DropdownMenuItem(
                                    text = { Text(storeName) },
                                    onClick = {
                                        selectedStore.value = storeId
                                        isDropdownExpanded.value = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // se o horario esta aberto ou nao
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Aberto:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), shape = CircleShape)
                                .clickable {
                                    isOpenExpanded.value = true
                                }
                        ){
                            Text(
                                text = isOpenSelected.value?.let { if (it) "Sim" else "Não" } ?: "Selecionar",
                                modifier = Modifier
                                    .background(Color(0xFFF0F0F0))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                color = Color.Black
                            )
                        }
                        DropdownMenu(
                            expanded = isOpenExpanded.value,
                            onDismissRequest = {isOpenExpanded.value = false},
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sim") },
                                onClick = {
                                    isOpenSelected.value = true
                                    isOpenExpanded.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Não") },
                                onClick = {
                                    isOpenSelected.value = false
                                    isOpenExpanded.value = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // se nem todos os campos foram preenchidos
                        if (date.value.isEmpty() || startTime.value.isEmpty() || endTime.value.isEmpty() || selectedStore.value == null || isOpenSelected.value == null) {
                            Toast.makeText(
                                context,
                                "Por favor, preencha todos os campos.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val selectedDate = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            ).parse(date.value)
                            val startCalendar = Calendar.getInstance().apply {
                                if (selectedDate != null) {
                                    time = selectedDate
                                }
                                val (hour, minute) = startTime.value.split(":").map { it.toInt() }
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                            }
                            val endCalendar = Calendar.getInstance().apply {
                                if (selectedDate != null) {
                                    time = selectedDate
                                }
                                val (hour, minute) = endTime.value.split(":").map { it.toInt() }
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                            }
                            //adicionar horario
                            viewModel.addSchedule(
                                Timestamp(startCalendar.time),
                                Timestamp(endCalendar.time),
                                selectedStore.value!!,
                                isOpenSelected.value!!
                            )
                            showAdicionar.value = false
                        }
                    }
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                Button(onClick = { showAdicionar.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    //endregion

    //endregion
}

// secçao horarios (abertos/fechados)
@Composable
fun ScheduleSection(navController: NavHostController, title: String, schedules: List<Schedules>, stores: List<Pair<String, String>>, isGestor: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (schedules.isEmpty()) {
            Text("Nenhum horário disponível.")
        } else {
            Column {
                schedules.forEachIndexed{ _, schedule ->
                    ScheduleItem(navController, schedule, stores, isGestor)
                }
            }
        }
    }
}

// cada horario
@Composable
fun ScheduleItem(navController: NavHostController, schedule: Schedules, stores: List<Pair<String, String>>, isGestor: Boolean) {
    val storeName = stores.find { it.first == schedule.storeId }?.second ?: "Desconhecido"
    val isExpanded = remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("dd 'de' MMMM", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = dateFormat.format(schedule.dateStart.toDate())
    val startTime = timeFormat.format(schedule.dateStart.toDate())
    val endTime = timeFormat.format(schedule.dateEnd.toDate())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .clickable { isExpanded.value = !isExpanded.value }
            .padding(16.dp)
    ) {
        // Data, Tempo, Nome da Loja, Dropdown Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //date e tempo
            Column {
                Text(
                    text = date,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = "$startTime - $endTime",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            //nome da loja e dropdown icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = storeName,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        // region dropdown
        if (isExpanded.value) {
            if(isGestor){
                GestorIcon(navController, schedule)
            } else {
                VolunteerDrop(schedule)
            }
        }
        //endregion
    }

}

//opcoes da gestora
@Composable
fun GestorIcon(navController: NavHostController, schedule: Schedules){
    val context = LocalContext.current
    val viewModel: HorariosViewModel = viewModel()
    val showDeleteDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (schedule.open) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ManageAccounts,
                    contentDescription = "Manage",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            navController.navigate("candidatura_horario/${schedule.id}")
                        }
                )
                Text(
                    text = "Gerir Candidaturas",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (schedule.open) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = if (schedule.open) "Close" else "Open",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        // Se estiver fechado e já passou não deixa abrir o horário
                        if(!schedule.open && schedule.dateStart.toDate().before(Date())){
                            Toast.makeText(
                                context,
                                "Data do horário já terminou.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.toggleScheduleStatus(schedule.id, !schedule.open)
                        }
                    }
            )
            Text(
                text = "Abrir/Fechar",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier
                    .size(24.dp)
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
    //region delete
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(
                    text = "Apagar Horário",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Tem certeza de que deseja apagar este horário?",
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
                                viewModel.deleteSchedule(schedule.id)
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
//opcoes do voluntario
@Composable
fun VolunteerDrop(schedule: Schedules){
    val viewModel: HorariosViewModel = viewModel()
    val context = LocalContext.current

    //cargo selecionado
    val selectedPosition = remember { mutableStateOf("Selecionar Cargo") }
    val selectedPositionId = remember { mutableStateOf<String?>(null) }
    //numero de candidaturas ao horario
    val entriesCount = remember { mutableStateOf(0) }
    //cargos possiveis
    val positions = remember { mutableStateOf<List<Positions>>(emptyList()) }

    val isPositionExpanded = remember { mutableStateOf(false) } // dropdown para cargo

    LaunchedEffect(Unit){
        //obter os cargos possiveis
        viewModel.fetchPositions{ fetchedPositions ->
            positions.value = fetchedPositions
            Log.e("Cargos:", fetchedPositions.toString())
        }
        //obter o numero de candidaturas para o horario
        viewModel.getEntriesCountForSchedule(schedule.id){ count ->
            entriesCount.value = count
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column (
            modifier = Modifier
                .clickable { isPositionExpanded.value = true }
        ){
            Text(
                text = selectedPosition.value,
                modifier = Modifier
                    .background(Color(0xFFFFFFFF), shape = CircleShape)
                    .padding(8.dp)
                    .widthIn(max = 200.dp)
            )
            //selecionar cargo
            DropdownMenu(
                expanded = isPositionExpanded.value,
                onDismissRequest = { isPositionExpanded.value = false },
                modifier = Modifier.widthIn(max = 200.dp)
            ) {
                positions.value.forEach { position ->
                    DropdownMenuItem(
                        onClick = {
                            selectedPosition.value = position.name
                            selectedPositionId.value = position.id
                            isPositionExpanded.value = false
                        },
                        text = { Text(position.name)}
                    )
                }
            }
        }

        Text(
            text = entriesCount.value.toString(),
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = "Number of Applications",
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send Application",
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    // Verificar se o horário está aberto ou data válida
                    if (schedule.dateStart.toDate().before(Date()) || !schedule.open) {
                        Toast.makeText(
                            context,
                            "Horário fechado. Não pode enviar candidatura.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    // Verificar se o cargo foi selecionado
                    else if (selectedPositionId.value.isNullOrEmpty()) {
                        Toast.makeText(
                            context,
                            "Por favor, selecione um cargo antes de enviar a candidatura.",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        // adicionar candidatura
                        viewModel.addEntry(selectedPositionId.value!!, schedule.id)
                        Toast.makeText(
                            context,
                            "Candidatura enviada.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        )
    }
}