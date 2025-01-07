package com.example.myapplication.presentation.screens

import android.app.DatePickerDialog
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.domain.model.Donations
import com.example.myapplication.presentation.viewModels.DoacoesViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DoacoesScreen(navController: NavHostController){
    //region variables
    val viewModel: DoacoesViewModel = viewModel()
    val context = LocalContext.current
    //pop-ups
    val showAdicionar = remember { mutableStateOf(false) }
    val showEntidade = remember { mutableStateOf(false) }
    val showDonor = remember { mutableStateOf(false) }
    val showAnonymous = remember { mutableStateOf(false) }

    val entidadeExpanded = remember { mutableStateOf(false) }
    val entidadeSelecionada = remember { mutableStateOf<String?>(null) }
    val entities = viewModel.entities.collectAsState(emptyList())

    val notes = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    // dados dos doadores
    val donorEmail = remember { mutableStateOf("") }
    val donorName = remember { mutableStateOf("") }
    val donorPhone = remember { mutableStateOf("") }
    //doacoes na firestore
    val donations = remember { mutableStateOf<List<Donations>>(emptyList()) }
    //endregion

    //obter todas as doacoes
    LaunchedEffect(Unit) {
        viewModel.fetchDonations { donationsList ->
            donations.value = donationsList
        }
    }

    //region main
    Column(modifier = Modifier.fillMaxSize()) {
        //region top
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
                    text = "Doações",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
                // Icon "+" para adicionar Stock
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
                        contentDescription = "Add Donation",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        //region adicionar produto
        if (showAdicionar.value) {
            AlertDialog(
                onDismissRequest = { showAdicionar.value = false },
                title = { Text("Adicionar Pedido") },
                text = {
                    Column {
                        // butoes para "Entidade", "Doador" e "Anonimo"
                        if (!showEntidade.value && !showDonor.value && !showAnonymous.value) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Button(
                                    onClick = { showEntidade.value = true }
                                ) {
                                    Text("Entidade")
                                }
                                Button(
                                    onClick = { showDonor.value = true }
                                ) {
                                    Text("Doador")
                                }
                                Button(
                                    onClick = { showAnonymous.value = true }
                                ) {
                                    Text("Anónimo")
                                }
                            }
                        }

                        // Entidade Escolhida - Dropdown
                        if (showEntidade.value) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Entidade:",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(80.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .clickable { entidadeExpanded.value = true }
                                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = entidadeSelecionada.value ?: "Selecione entidade",
                                        fontSize = 14.sp
                                    )
                                    DropdownMenu(
                                        expanded = entidadeExpanded.value,
                                        onDismissRequest = { entidadeExpanded.value = false }
                                    ) {
                                        entities.value.forEach { entity ->
                                            Text(
                                                text = entity.name,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        entidadeSelecionada.value = entity.name
                                                        entidadeExpanded.value = false
                                                    }
                                                    .padding(4.dp),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        //Doador Escolhido - TextFields
                        if (showDonor.value) {
                            Column {
                                TextField(
                                    value = donorEmail.value,
                                    onValueChange = { donorEmail.value = it },
                                    label = { Text("Email") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextField(
                                    value = donorName.value,
                                    onValueChange = { donorName.value = it },
                                    label = { Text("Nome") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextField(
                                    value = donorPhone.value,
                                    onValueChange = { donorPhone.value = it },
                                    label = { Text("Telefone") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        //doador anonimo
                        if (showAnonymous.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Doador Anónimo",
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                )
                            }
                        }
                        // Campo "Notas"
                        Column(
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            TextField(
                                value = notes.value,
                                onValueChange = { notes.value = it },
                                label = { Text("Notas") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                maxLines = 5
                            )
                            Spacer(modifier = Modifier.height((8.dp)))
                            // Escolher data
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text("Data:", fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF0F0F0))
                                        .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
                                        .clip(RoundedCornerShape(8.dp))
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
                        }

                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            // Por valores a null
                            showAdicionar.value = false
                            showEntidade.value = false
                            showDonor.value = false
                            showAnonymous.value = false

                            date.value = ""
                            entidadeSelecionada.value = null
                            notes.value = ""
                            donorEmail.value = ""
                            donorName.value = ""
                            donorPhone.value = ""
                        }
                    ) {
                        Text("Cancelar")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            //nenhum tipo de doador foi selecionado
                            if (!showEntidade.value && !showDonor.value && !showAnonymous.value) {
                                Toast.makeText(context, "Por favor, selecione uma das opções: Entidade, Doador ou Anónimo.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            // dado obrigatorio para entidade
                            if (showEntidade.value && entidadeSelecionada.value == null) {
                                Toast.makeText(context, "Por favor, selecione uma entidade.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            //pelo menos um campo obrigatorio para doadores
                            if (showDonor.value && (donorName.value.isEmpty() && donorEmail.value.isEmpty() && donorPhone.value.isEmpty())) {
                                Toast.makeText(context, "Por favor, insira informações do doador.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            //campos obrigatorios
                            if (notes.value.isEmpty()) {
                                Toast.makeText(context, "Por favor, insira notas.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (date.value.isEmpty()) {
                                Toast.makeText(context, "Por favor, selecione uma data.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val phoneNum = donorPhone.value.toIntOrNull()
                            val entidadeId = entities.value.find { it.name == entidadeSelecionada.value }?.id

                            //adiciona noca doação
                            viewModel.addDonation(
                                entidadeId = entidadeId,
                                donorName = donorName.value,
                                donorEmail = donorEmail.value,
                                donorPhone = phoneNum,
                                notes = notes.value,
                                date = date.value,
                            )
                            // Por valores a null
                            showAdicionar.value = false
                            showEntidade.value = false
                            showDonor.value = false
                            showAnonymous.value = false

                            date.value = ""
                            entidadeSelecionada.value = null
                            notes.value = ""
                            donorEmail.value = ""
                            donorName.value = ""
                            donorPhone.value = ""
                        }
                    ) {
                        Text("Confirmar")
                    }
                }
            )
        }
        //endregion
        // Lista das doacoes
        Column(modifier = Modifier.fillMaxSize()) {
            TableDon(donations.value)
        }
    }
}

@Composable
fun TableDon(donations: List<Donations>) {
    Column(modifier = Modifier.fillMaxSize()) {
        //Header com os titulos das colunas
        DonationsHeader()
        // row por doacao
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(donations) { donation ->
                DonationsTableRow(donation)
            }
        }
    }
}
//campos com os titulos das colunas
@Composable
fun DonationsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Data",
            modifier = Modifier.weight(2.75f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Doação",
            modifier = Modifier.weight(7f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "",
            modifier = Modifier.weight(0.5f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
//Row por doação no firestore
@Composable
fun DonationsTableRow(donation: Donations) {
    //region variaveis
    val viewModel: DoacoesViewModel = viewModel()
    val context = LocalContext.current
    //amostra as opcoes
    val isExpanded = remember { mutableStateOf(false) }
    //pop-ups para as opções
    val showSeeMoreDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    //valores para editar
    val formattedDate = donation.date.toDate().let {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
    }
    var newNotes by remember { mutableStateOf(donation.notes) }
    var newDate by remember { mutableStateOf(formattedDate) }
    var newDonorName by remember { mutableStateOf(donation.donorName) }
    var newDonorEmail by remember { mutableStateOf(donation.donorEmail) }
    var newDonorPhone by remember { mutableStateOf(donation.donorPhoneNo?.toString() ?: "") }
    //endregion

    //region dropdown
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
            .clickable { isExpanded.value = !isExpanded.value }
            .padding(4.dp)
    ){
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(donation.date.toDate()),
                    modifier = Modifier.weight(2.75f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = donation.notes,
                    modifier = Modifier.weight(7f)
                        .padding(start = 4.dp),
                    textAlign = TextAlign.Start,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.weight(0.5f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ){
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }
            }
            //amostra os icons
            if (isExpanded.value) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Ver Mais
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = "See More",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        showSeeMoreDialog.value = true
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
                            //Editar doação
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
                            //Apagar doação
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
        }
    }
    //endregion
    //region Ver Mais
    if (showSeeMoreDialog.value){
        AlertDialog(
            onDismissRequest = { showSeeMoreDialog.value = false },
            title = {
                Text(
                    text = "Detalhes da Doação",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column {
                    val entities by viewModel.entities.collectAsState()
                    val entity = entities.find { it.id == donation.entitiesId }
                    val isEntity = entity != null
                    val isAnonymous = donation.donorName.isEmpty() &&
                            donation.donorEmail.isEmpty() &&
                            donation.donorPhoneNo == null

                    if (isEntity) {
                        //o doador é uma entidade
                        Text(buildAnnotatedString {
                            appendBoldLabel("Entidade: ")
                            append(entity?.name ?: "Desconhecido")
                        })
                    } else if (isAnonymous) {
                        // o doador é anónimo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ){
                            Text("Doador anónimo", textAlign = TextAlign.Center)
                        }
                    } else {
                        // Campos para o doador
                        if (donation.donorName.isNotEmpty()) {
                            Text(buildAnnotatedString {
                                appendBoldLabel("Nome do Doador: ")
                                append(donation.donorName)
                            })
                        }
                        if (donation.donorEmail.isNotEmpty()) {
                            Text(buildAnnotatedString {
                                appendBoldLabel("Email do Doador: ")
                                append(donation.donorEmail)
                            })
                        }
                        donation.donorPhoneNo?.let { phoneNo ->
                            Text(buildAnnotatedString {
                                appendBoldLabel("Telefone do Doador: ")
                                append(phoneNo.toString())
                            })
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    //Notas
                    Row {
                        Text(
                            text = "Notas: ",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(64.dp)
                        )
                        Text(
                            text = donation.notes
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    //Data
                    Text(buildAnnotatedString {
                        appendBoldLabel("Data: ")
                        append(formattedDate)
                    })
                }
            },
            confirmButton = {
                Button(onClick = { showSeeMoreDialog.value = false }) {
                    Text("Fechar")
                }
            }
        )
    }
    //endregion

    //region Editar
    if (showEditDialog.value){
        val entities by viewModel.entities.collectAsState()
        val entity = entities.find { it.id == donation.entitiesId }
        val isEntity = entity != null
        val isAnonymous = donation.donorName.isEmpty() &&
                donation.donorEmail.isEmpty() &&
                donation.donorPhoneNo == null
        AlertDialog(
            onDismissRequest = {showEditDialog.value = false},
            title = {
                Text(
                    text = "Editar Doação",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (isEntity) {
                        // o doador é uma entidade
                        Text(buildAnnotatedString {
                            appendBoldLabel("Entidade: ")
                            append(entity?.name ?: "Desconhecido")
                        })
                    } else if (isAnonymous) {
                        //doador anónimo
                        Text("Doador Anônimo", textAlign = TextAlign.Center)
                    } else {
                        // Campos do doador
                        Column(
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ){
                                Text("Nome Doador:",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(90.dp)
                                )
                                CompactTextField(
                                    value = newDonorName,
                                    onValueChange = { newDonorName = it },
                                    placeholder = "Atualize o nome",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ){
                                Text("Email Doador:",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(90.dp)
                                )
                                CompactTextField(
                                    value = newDonorEmail,
                                    onValueChange = { newDonorEmail = it },
                                    placeholder = "Atualize o email",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ){
                                Text("Telefone Doador:",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(90.dp)
                                )
                                CompactTextField(
                                    value = newDonorPhone,
                                    onValueChange = { newDonorPhone = it },
                                    placeholder = "Atualize o telefone",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                    //Notas e Data
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Text("Notas:",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(90.dp)
                            )
                            CompactTextField(
                                value = newNotes,
                                onValueChange = { newNotes = it },
                                placeholder = "Atualize as notas",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Text("Data:",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(90.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF0F0F0))
                                    .border(1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        val calendar = Calendar.getInstance()
                                        DatePickerDialog(
                                            context,
                                            { _, year, month, dayOfMonth ->
                                                calendar.set(year, month, dayOfMonth)
                                                newDate = SimpleDateFormat(
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
                                    text = newDate.ifEmpty { "Atualize a data" },
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEditDialog.value = false }
                ) {
                    Text("Cancelar")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        //se nenhuma alteracao foi feita
                        if (
                            newDonorName == donation.donorName &&
                            newDonorEmail == donation.donorEmail &&
                            newDonorPhone == donation.donorPhoneNo.toString() &&
                            newNotes ==donation.notes &&
                            newDate == donation.date.toString())
                        {
                            Toast.makeText(context, "Nenhuma alteração foi feita", Toast.LENGTH_SHORT).show()
                        }
                        //atualiza doação
                        viewModel.updateDonation(
                            donation.id,
                            newNotes,
                            newDate,
                            if (!isEntity && !isAnonymous) newDonorName else null,
                            if (!isEntity && !isAnonymous) newDonorEmail else null,
                            if (!isEntity && !isAnonymous) newDonorPhone.toIntOrNull() else null
                        )
                        showEditDialog.value = false
                    }
                ) {
                    Text("Confirmar")
                }
            }
        )
    }
    //endregion
    //region apagar doacao
    if (showDeleteDialog.value){
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(
                    text = "Apagar Doação",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Tem certeza de que deseja apagar esta doação?",
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
                                viewModel.deleteDonation(donation.id)
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