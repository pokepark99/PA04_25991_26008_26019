package com.example.myapplication.presentation.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.domain.utils.CheckConnectionUtil
import com.example.myapplication.domain.utils.ImageUtils
import com.example.myapplication.presentation.viewModels.PerfilViewModel


@Composable
fun PerfilScreen(navController: NavHostController, userId:String?){
    //region variables
    val mainActivity = LocalContext.current as MainActivity
    val viewModel: PerfilViewModel = viewModel()
    val context = LocalContext.current
    var userData = viewModel.userData
    val userEmail = viewModel.getCurrentUserEmail(mainActivity)
    val country = remember { mutableStateOf("") }
    val showEditDialog = remember { mutableStateOf(false) }

    var selectedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val bitmap = ImageUtils.processImageAsWebP(it, context)
                if (bitmap != null) {
                    if(CheckConnectionUtil.isConnected(mainActivity)) {
                        selectedImageBitmap = bitmap
                        if (userData != null) {
                            val updatedUser = userData.copy(
                                photo = ImageUtils.bitmapToBase64(bitmap)
                            )
                            viewModel.updateUserProfile(userData.id, updatedUser)
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Por favor selecione uma imagem com tamanho inferior a 200 KB.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    )
    //endregion

    LaunchedEffect(Unit){
        userId?.let {
            viewModel.getCurrentUser(mainActivity, userId)
        }
    }
    LaunchedEffect(userData?.countriesId) {
        userData?.countriesId?.let { countriesId ->
            viewModel.fetchCountryName(countriesId) { name ->
                country.value = name ?: "Desconhecido"
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                    text = "Perfil",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
                // Icon para editar dados
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(1.dp, Color.Black, shape = CircleShape)
                        .clickable {
                            showEditDialog.value = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Info",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        //endregion

        //se tiver os dados do utilizador
        userData?.let { user ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {
                //foto e nome do utilizador
                val bitmap = ImageUtils.base64ToBitmap(user.photo)
                if (bitmap != null) {
                    selectedImageBitmap = bitmap
                }
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .weight(0.75f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    selectedImageBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Selected Image"
                        )
                    } ?: run {
                        Image(
                            painter = painterResource(id = R.drawable.profile_image_placeholder),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .background(Color.Gray)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = user.name,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1.25f)
                        .padding(start = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Row para Contacto e Email
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabelledTextField(
                    label = "Contacto",
                    value = (user.phoneNo).toString(),
                    modifier = Modifier.weight(1f)
                )
                LabelledTextField(
                    label = "Email",
                    value = userEmail ?: "Email não encontrado",
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
            }
            // Row para DOB e Cidade
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabelledTextField(
                    label = "Data de Nascimento",
                    value = user.dob,
                    modifier = Modifier.weight(1f)
                )

                LabelledTextField(
                    label = "Cidade",
                    value = userData.city,
                    modifier = Modifier.weight(1f)
                )
            }
            // Row para Pais e NIF
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabelledTextField(
                    label = "Pais",
                    value = country.value,
                    modifier = Modifier.weight(1f)
                )
                LabelledTextField(
                    label = "NIF",
                    value = (userData.nif).toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    //region Editar
    if (showEditDialog.value){
        val updatedPhone = remember { mutableStateOf(userData?.phoneNo?.toString() ?: "") }
        val updatedDob = remember { mutableStateOf(userData?.dob ?: "") }
        val updatedCity = remember { mutableStateOf(userData?.city ?: "") }
        val updatedCountry = remember { mutableStateOf(country.value) }
        val updatedNif = remember { mutableStateOf(userData?.nif?.toString() ?: "") }

        AlertDialog(
            onDismissRequest = { showEditDialog.value = false },
            title = {
                Text(
                    text = "Editar Dados",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    EditableTextField(label = "Contacto", value = updatedPhone.value) {
                        updatedPhone.value = it
                    }
                    EditableTextField(label = "Data de Nascimento", value = updatedDob.value) {
                        updatedDob.value = it
                    }
                    EditableTextField(label = "Cidade", value = updatedCity.value) {
                        updatedCity.value = it
                    }
                    EditableTextField(label = "País", value = updatedCountry.value) {
                        updatedCountry.value = it
                    }
                    EditableTextField(label = "NIF", value = updatedNif.value) {
                        updatedNif.value = it
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
                        var isUpdated = false
                        //verifica se algum campo mudou
                        if (userData?.phoneNo.toString() != updatedPhone.value) isUpdated = true
                        if (userData?.dob != updatedDob.value) isUpdated = true
                        if (userData?.city != updatedCity.value) isUpdated = true
                        if (country.value != updatedCountry.value) isUpdated = true
                        if (userData?.nif.toString() != updatedNif.value) isUpdated = true

                        // verifica o formato de dob
                        val dobRegex = """\d{2}/\d{2}/\d{4}""".toRegex()
                        if (updatedDob.value.isNotEmpty() && !dobRegex.matches(updatedDob.value)) {
                            Toast.makeText(
                                context,
                                "Data de Nascimento inválida. Use o formato dd/MM/yyyy.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        //se nenhum campo foi alterado
                        if (!isUpdated) {
                            Toast.makeText(
                                context,
                                "Nenhuma alteração detectada.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if(CheckConnectionUtil.isConnected(context)) {
                            viewModel.updateUserProfile(
                                updatedPhone.value.toInt(),
                                updatedDob.value,
                                updatedCity.value,
                                updatedCountry.value,
                                updatedNif.value.toLongOrNull() ?: 0L,
                                onSuccess = {
                                    Toast.makeText(context, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                                    showEditDialog.value = false
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(context, "Erro ao atualizar perfil: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            )

                            showEditDialog.value = false
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            }

        )
    }
    //endregion
}

// campos de texto com a info dos utilizadores
@Composable
fun LabelledTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Label
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Content Box
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Non-editable Text Field
            BasicTextField(
                value = value,
                onValueChange = {},
                readOnly = readOnly,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

//campos de texto para editar os dados do utilizador
@Composable
fun EditableTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                .padding(8.dp)
        )
    }
}