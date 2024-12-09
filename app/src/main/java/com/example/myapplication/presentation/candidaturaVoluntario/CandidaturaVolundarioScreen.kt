package com.example.myapplication.presentation.candidaturaVoluntario

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import java.util.Calendar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavHostController

@Composable
fun CandidaturaVoluntarioScreen(navController: NavHostController){
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var nif by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("")}

    // scroll
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)  // Enable scrolling
    ) {
        // Imagem do logo
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, bottom = 16.dp),
            painter = painterResource(id = R.drawable.lojalogo),
            contentDescription = "O logo da loja social"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 135.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Texto de descricao
            Text(
                text = "Nós da Loja Social São Lázaro e São João do Souto, procuramos pessoas comprometidas e com vontade de ajudar esta causa. Se tens interesse a juntar te a esta equipa  todos os sábados inscreve-te!\n" +
                        "Vemos te em breve!",
                color = Color(0xFF695F5F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 5.dp),
                fontSize = 8.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Row para a foto, nome e email
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // foto
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Gray.copy(alpha = 0.2f))
                        .clickable { /* Add functionality for photo picker */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Adicionar Foto",
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // email e pass
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LabelledTextField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        keyboardType = KeyboardType.Email
                    )
                    LabelledTextField(
                        label = "Password",
                        value = password,
                        onValueChange = { password = it }
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Row para DOB e NIF
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabelledTextField(
                    label = "Nome",
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.weight(1f)
                )
                LabelledTextField(
                    label = "Data de Nascimento",
                    value = dob,
                    onValueChange = { dob = it },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Row para City e Country
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabelledTextField(
                    label = "Cidade",
                    value = city,
                    onValueChange = { city = it },
                    modifier = Modifier.weight(1f)
                )

                LabelledTextField(
                    label = "País de Origem",
                    value = country,
                    onValueChange = { country = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Row para Contanto e Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LabelledTextField(
                    label = "Contacto",
                    value = contact,
                    onValueChange = { contact = it },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Phone
                )

                LabelledTextField(
                    label = "NIF",
                    value = nif,
                    onValueChange = { nif = it },
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // butao submissao
            Button(
                onClick = { /* adicionar funcao para submeter candidatura */ },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC1F26))
            ) {
                Text(text = "Enviar", color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun LabelledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
                .border(1.dp, Color.Gray, shape = MaterialTheme.shapes.small)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .let {
                    if (label == "Data de Nascimento") it.clickable { showDatePicker.value = true }
                    else it
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (label == "Data de Nascimento") {
                    Text(
                        text = value,
                        style = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Abrir Calendario",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                } else if(label == "Password"){
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.weight(1f)
                    )

                } else {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
    // Date Picker Dialog
    if (showDatePicker.value) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                onValueChange("$selectedDay/${selectedMonth + 1}/$selectedYear")
                showDatePicker.value = false
            },
            year,
            month,
            day
        ).apply {
            setOnCancelListener {
                showDatePicker.value = false // reinicia o estado quando fecha
            }
        }.show()
    }
}
