package com.example.myapplication.presentation.screens

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.domain.model.Items
import com.example.myapplication.domain.utils.CheckConnectionUtil
import com.example.myapplication.presentation.viewModels.StockViewModel

@Composable
fun StockScreen(navController: NavHostController) {
    //region variaveis
    val viewModel: StockViewModel = viewModel()
    val context = LocalContext.current

    //adicionar produto
    val showAdicionar = remember { mutableStateOf(false) }
    val produto = remember { mutableStateOf("") }
    val categoriaExpanded = remember { mutableStateOf(false) }
    val categoriaSelecionada = remember { mutableStateOf<String?>(null) }
    val itemTypes = viewModel.itemTypes.collectAsState(emptyList())
    val stock = remember { mutableStateOf("") }

    //pesquisar pelo nome do produto
    var searchText by remember { mutableStateOf("") }
    //filtros
    var showSortDialog by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf("Nome: Crescente") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) } //categorias para filtrar
    //endregion

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
                    text = "Inventário",
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
                        contentDescription = "Add Stock",
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
                title = { Text("Adicionar Produto") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Producto nome
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Produtos:",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(80.dp)
                            )
                            CompactTextField(
                                value = produto.value,
                                onValueChange = { produto.value = it },
                                placeholder = "Nome do produto",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                        // categoria do produto - dropdown
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Categoria:",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(80.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clickable { categoriaExpanded.value = true }
                                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = categoriaSelecionada.value ?: "Selecione categoria",
                                    fontSize = 14.sp
                                )
                                DropdownMenu(
                                    expanded = categoriaExpanded.value,
                                    onDismissRequest = { categoriaExpanded.value = false }
                                ) {
                                    itemTypes.value.forEach { itemType ->
                                        Text(
                                            text = itemType.description,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    categoriaSelecionada.value = itemType.description
                                                    categoriaExpanded.value = false
                                                }
                                                .padding(4.dp),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                        // inventorio/stock
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Stock:",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(80.dp)
                            )
                            CompactTextField(
                                value = stock.value,
                                onValueChange = { stock.value = it },
                                placeholder = "Quantidade",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showAdicionar.value = false
                        produto.value = ""
                        categoriaSelecionada.value = null
                        stock.value = ""
                    }) {
                        Text("Cancelar")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val produtoName = produto.value.trim()
                            val categoria = categoriaSelecionada.value
                            val stockValue = stock.value.trim()

                            if (produtoName.isEmpty() || categoria == null || stockValue.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Por favor, preencha todos os campos.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if(CheckConnectionUtil.isConnected(context)) {
                                    viewModel.addProduct(
                                        name = produtoName,
                                        itemTypeName = categoria,
                                        stock = stockValue.toIntOrNull() ?: 0
                                    ) { success ->
                                        if (success){
                                            Toast.makeText(context, "Produto adicionado com sucesso", Toast.LENGTH_SHORT).show()
                                            produto.value = ""
                                            categoriaSelecionada.value = null
                                            stock.value = ""
                                        }
                                        else {
                                            Toast.makeText(context, "Erro ao adicionar produto", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    showAdicionar.value = false
                                }
                            }
                        }
                    ) {
                        Text("Adicionar")
                    }
                }
            )
        }
        //endregion
        //endregion

        Row (verticalAlignment = Alignment.CenterVertically) {
            // Campo para procura por nome do produto
            TextField(
                value = searchText,
                onValueChange = { text ->
                    searchText = text
                    viewModel.filterProducts(text)
                },
                label = { Text("Pesquisar por produto") },
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
        Column(modifier = Modifier.fillMaxSize()) {
            StockTable(viewModel)
        }

        //region ordenar
        if (showSortDialog) {
            AlertDialog(
                onDismissRequest = { showSortDialog = false },
                title = { Text("Filtrar Inventário") },
                text = {
                    Column {
                        // opcoes para ordenar
                        Text("Ordenar por:")
                        listOf("Nome: Crescente", "Nome: Decrescente").forEach { filter ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSortOption = filter }
                            ) {
                                RadioButton(
                                    selected = selectedSortOption == filter,
                                    onClick = { selectedSortOption = filter }
                                )
                                Text(filter, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // filtrar por categorias
                        Text("Categorias:")
                        itemTypes.value.chunked(2).forEach { rowCategories -> // agrupa as categorias em pares de 2
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowCategories.forEach { category ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                if (selectedCategories.contains(category.description)) {
                                                    selectedCategories = selectedCategories - category.description
                                                } else {
                                                    selectedCategories = selectedCategories + category.description
                                                }
                                            }
                                    ) {
                                        Checkbox(
                                            checked = selectedCategories.contains(category.description),
                                            onCheckedChange = { isChecked ->
                                                selectedCategories = if (isChecked) {
                                                    selectedCategories + category.description
                                                } else {
                                                    selectedCategories - category.description
                                                }
                                            }
                                        )
                                        Text(category.description)
                                    }
                                }
                                if (rowCategories.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.sortProducts(selectedSortOption, selectedCategories.toList())
                            showSortDialog = false
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showSortDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
        //endregion
    }
    //endregion
}

//Textfield para adicionar(nome do produto e stock)
@Composable
fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = false, // permite varias linhas
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = LocalTextStyle.current.copy(color = Color.Gray, fontSize = 14.sp)
                    )
                }
                innerTextField()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StockTable(viewModel: StockViewModel) {
    val itemTypes by viewModel.itemTypes.collectAsState()
    val items by viewModel.filteredProducts.collectAsState() //produtos filtrados

    // mapear as categorias ao seu id
    val itemTypeMap = itemTypes.associate { it.id to it.description }

    Column {
        TableHeader()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                TableRow(item, itemTypeMap, viewModel)
            }
        }
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Categoria",
            modifier = Modifier.weight(2.5f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Produto",
            modifier = Modifier.weight(7f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "",
            modifier = Modifier.weight(1.25f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun TableRow(item: Items, itemTypeMap: Map<String, String>, viewModel: StockViewModel) {
    val context = LocalContext.current

    val newName = remember { mutableStateOf(item.name) }
    val newStock = remember { mutableStateOf(item.stock.toString()) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = itemTypeMap[item.itemTypeId] ?: "Desconhecido",
            modifier = Modifier.weight(2.5f),
            textAlign = TextAlign.Center
        )
        Text(
            text = item.name,
            modifier = Modifier.weight(6f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "x${item.stock}",
            modifier = Modifier.weight(1.25f),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Edit Icon
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showEditDialog.value = true }
            )
            // Delete Icon
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Deletar",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { showDeleteDialog.value = true }
            )
        }
    }
    //region editar
    if (showEditDialog.value) {
        AlertDialog(
            onDismissRequest = { showEditDialog.value = false },
            title = { Text("Editar Produto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Edit Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Nome:", fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                        CompactTextField(
                            value = newName.value,
                            onValueChange = { newName.value = it },
                            placeholder = "Novo nome",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    // Edit Stock
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Stock:", fontWeight = FontWeight.Bold, modifier = Modifier.width(80.dp))
                        CompactTextField(
                            value = newStock.value,
                            onValueChange = { newStock.value = it },
                            placeholder = "Nova quantidade",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedName = newName.value.trim()
                        val updatedStock = newStock.value.trim().toIntOrNull()

                        if (updatedName.isEmpty() || updatedStock == null) {
                            Toast.makeText(context, "Por favor, preencha os campos corretamente.", Toast.LENGTH_SHORT).show()
                        } else {
                            if(CheckConnectionUtil.isConnected(context)) {
                                viewModel.updateProduct(
                                    id = item.id,
                                    name = updatedName,
                                    stock = updatedStock
                                ) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Produto atualizado com sucesso", Toast.LENGTH_SHORT).show()
                                        showEditDialog.value = false
                                    } else {
                                        Toast.makeText(context, "Erro ao atualizar o produto.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    //endregion
    //region apagar produto
    if (showDeleteDialog.value){
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = {
                Text(
                    text = "Confirmar Apagar Produto",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "Tem certeza de que deseja apagar este produto?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text(text = "Cancelar")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if(CheckConnectionUtil.isConnected(context)) {
                            viewModel.deleteProduct(item.id)
                            showDeleteDialog.value = false
                        }
                    }
                ) {
                    Text(text = "Apagar")
                }
            }

            )
    }
    //endregion
}