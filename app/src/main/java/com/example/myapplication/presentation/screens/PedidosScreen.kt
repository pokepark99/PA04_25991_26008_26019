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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.domain.model.Requests
import com.example.myapplication.domain.model.Visitors
import com.example.myapplication.presentation.viewModels.PedidosViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PedidosScreen(navController: NavHostController){
    //region variaveis
    val viewModel: PedidosViewModel = viewModel()
    val context = LocalContext.current

    val showAdicionar = remember { mutableStateOf(false) }
    val showEntidade = remember { mutableStateOf(false) }
    val showVisitor = remember { mutableStateOf(false) }

    val produto = remember { mutableStateOf("") }
    val quantidade = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    //dropdown das entidades
    val entidadeExpanded = remember { mutableStateOf(false) }
    val entidadeSelecionada = remember { mutableStateOf<String?>(null) }
    val entities = viewModel.entities.collectAsState(emptyList())
    //pesquisa
    val searchVisitor = remember { mutableStateOf("") }
    val visitorResults = remember { mutableStateOf<List<Visitors>>(emptyList()) }
    val selectedVisitor = remember { mutableStateOf<Visitors?>(null) }

    //pedidos na firestore
    val requests = remember { mutableStateOf<List<Requests>>(emptyList()) }
    //endregion

    //obter todos os pedidos ativos
    LaunchedEffect(Unit) {
        viewModel.fetchActiveRequests { requestsList ->
            requests.value = requestsList
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        //region Top
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
                    text = "Pedidos",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
                // Icon "+" para adicionar pedido
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
                        contentDescription = "Add Request",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        //endregion

        if (showAdicionar.value){
            AlertDialog(
                onDismissRequest = { showAdicionar.value = false },
                title = { Text("Adicionar Visita") },
                text = {
                    Column {
                        // butoes para "Entidade" e "Visitante"
                        if (!showEntidade.value && !showVisitor.value) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { showEntidade.value = true }
                                ) {
                                    Text("Entidade")
                                }
                                Button(
                                    onClick = { showVisitor.value = true }
                                ) {
                                    Text("Visitante")
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
                                        text = entidadeSelecionada.value ?: "Selecione categoria",
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
                        //Visitante Escolhido - Search
                        if (showVisitor.value) {
                            Column {
                                // Search Field
                                Text("Pesquisar Visitante")
                                TextField(
                                    value = searchVisitor.value,
                                    onValueChange = { search ->
                                        searchVisitor.value = search
                                        viewModel.searchVisitors(search) { results ->
                                            visitorResults.value = results
                                        }
                                    },
                                    placeholder = { Text("Nome ou TaxNO") }
                                )
                                LazyColumn(modifier = Modifier.fillMaxHeight(0.25f)) {
                                    items(visitorResults.value) { visitor ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedVisitor.value = visitor
                                                }
                                                .background(
                                                    if (selectedVisitor.value == visitor) Color.LightGray else Color.Transparent
                                                ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = visitor.name,
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .weight(1f)
                                            )
                                            Text(
                                                text = "TaxNO: ${visitor.taxNo}",
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Campos "Produto" e "Quantidade"
                        Column(
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Produto:",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(90.dp)
                                )
                                CompactTextField(
                                    value = produto.value,
                                    onValueChange = { produto.value = it },
                                    placeholder = "Identifique o produto",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Quantidade:",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(90.dp)
                                )
                                CompactTextField(
                                    value = quantidade.value,
                                    onValueChange = { quantidade.value = it },
                                    placeholder = "Quantidade",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
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
                            showAdicionar.value = false
                            showEntidade.value = false
                            showVisitor.value = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // se nem todos os campos foram preenchidos
                            if (date.value.isEmpty() || produto.value.isEmpty() || quantidade.value.isEmpty() ||
                                (selectedVisitor.value == null && entidadeSelecionada.value.isNullOrEmpty())) {
                                Toast.makeText(
                                    context,
                                    "Todos os campos devem ser preenchidos.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            // verifica que o valor em quantidade e um numero positivo
                            val quantity = quantidade.value.toIntOrNull()
                            if (quantity == null || quantity <= 0) {
                                Toast.makeText(
                                    context,
                                    "Quantidade deve ser um número positivo válido.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            val entidadeId = entities.value.find { it.name == entidadeSelecionada.value }?.id
                            val visitorId = selectedVisitor.value?.id

                            // Adicionar pedido
                            viewModel.addRequest(
                                date = date.value,
                                entidadeId = entidadeId,
                                visitorId = visitorId,
                                notes = produto.value,
                                quantity = quantity
                            )

                            // Por valores a null
                            showAdicionar.value = false
                            showEntidade.value = false
                            showVisitor.value = false
                            date.value = ""
                            entidadeSelecionada.value = null
                            selectedVisitor.value = null
                            produto.value = ""
                            quantidade.value = ""
                        }
                    ) {
                        Text("Confirmar")
                    }
                }
            )
        }
        // Lista dos pedidos
        Column(modifier = Modifier.fillMaxSize()) {
            Table(requests.value)
        }
    }
}

@Composable
fun Table(requests: List<Requests>) {
    Column(modifier = Modifier.fillMaxSize()) {
        //Header com os titulos das colunas
        THeader()
        // row por pedido
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(requests) { request ->
                TableRow(request)
            }
        }
    }
}
//campos com os titulos das colunas
@Composable
fun THeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Dia",
            modifier = Modifier.weight(2.75f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Pedido",
            modifier = Modifier.weight(6f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "",
            modifier = Modifier.weight(2f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
//Row por pedido ativo
@Composable
fun TableRow(request: Requests) {
    //region variaveis
    val viewModel: PedidosViewModel = viewModel()
    val context = LocalContext.current

    //amostra as opcoes
    val isExpanded = remember { mutableStateOf(false) }
    //pop-ups para editar ou apagar pedido
    val showEditDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    //valores para editar
    val updatedProduto = remember { mutableStateOf("") }
    val updatedQuantidade = remember { mutableStateOf("") }
    val updatedDate = remember { mutableStateOf("") }
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
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(request.dateStart.toDate()),
                    modifier = Modifier.weight(2.75f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = request.notes,
                    modifier = Modifier.weight(6f)
                        .padding(start = 4.dp),
                    textAlign = TextAlign.Start,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.weight(2f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "x${request.quantity}",
                        modifier = Modifier.padding(end = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
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
                            //Concluir pedido
                            Icon(
                                Icons.Default.Done,
                                contentDescription = "Conclude",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        viewModel.concludeRequest(request.id)
                                    }
                            )
                            Text(
                                text = "Concluir",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Editar pedido
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
                            //Apagar pedido
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
    //region editar pedido
    if (showEditDialog.value){
        AlertDialog(
            onDismissRequest = { showEditDialog.value = false },
            title = { Text("Adicionar Visita") },
            text = {
                Column {
                    // Campos para "Produto" e "Quantidade"
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Produto:",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(90.dp)
                            )
                            CompactTextField(
                                value = updatedProduto.value,
                                onValueChange = { updatedProduto.value = it },
                                placeholder = "Atualize o produto",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Quantidade:",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(90.dp)
                            )
                            CompactTextField(
                                value = updatedQuantidade.value,
                                onValueChange = { updatedQuantidade.value = it },
                                placeholder = "Atualize a quantidade",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height((8.dp)))
                        // Escolher data
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text("Nova Data:", fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))

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
                                                updatedDate.value = SimpleDateFormat(
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
                                    text = updatedDate.value.ifEmpty { "Selecionar Nova Data" },
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
                        showEditDialog.value = false
                    }
                ) {
                    Text("Cancelar")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // validar que pelo menos um campo foi preenchido
                        if (updatedDate.value.isEmpty() && updatedProduto.value.isEmpty() && updatedQuantidade.value.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Pelo menos um campo deve ser preenchido.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        // se a quantidade foi preenchida, validar que e um valor valido
                        if (updatedQuantidade.value != "") {
                            val quantity = updatedQuantidade.value.toIntOrNull()
                            if (quantity == null || quantity <= 0) {
                                Toast.makeText(
                                    context,
                                    "Quantidade deve ser um número positivo válido.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                        }

                        //passa apenas os dados nao vazios
                        viewModel.editRequest(
                            request.id,
                            date = updatedDate.value.takeIf { it.isNotEmpty() },
                            notes = updatedProduto.value.takeIf { it.isNotEmpty() },
                            quantity = updatedQuantidade.value.toIntOrNull()
                        )

                        // Por os valores como null
                        showEditDialog.value = false
                        updatedDate.value = ""
                        updatedProduto.value = ""
                        updatedQuantidade.value = ""
                    }
                ) {
                    Text("Confirmar")
                }
            }
        )
    }
    //endregion
    //region apagar pedido
    if (showDeleteDialog.value){
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(
                    text = "Apagar Pedido",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Tem certeza de que deseja apagar este pedido?",
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
                                viewModel.deleteRequest(request.id)
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