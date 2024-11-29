package com.example.myapplication.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val email = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }

        // Imagem do logo
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 32.dp)
                .height(150.dp),
            painter = painterResource(id = R.drawable.lojalogo),
            contentDescription = "O logo da loja social"
        )

        // Email
        TextField(
            value = "",
            onValueChange = { email.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .background(Color.White, shape = RoundedCornerShape(30.dp)),
            placeholder = { Text("Email") },
            label = { Text("Email") }
        )

        // Password
        TextField(
            value = "",
            onValueChange = { password.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .background(Color.White, shape = RoundedCornerShape(30.dp)),
            placeholder = { Text("Password") },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation() // Mask the password input
        )

        // Butao para Login
        Button(
            onClick = { /* Por funcao para verificar login */ },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(0.5f)  // Butao tem largura de 50% da largura
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC1F26))
        ) {
            Text(text = "Login", color = Color.White)
        }

        // Texto no fim do ecra
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 32.dp)
            ) {
                val annotatedString = buildAnnotatedString {
                    append("Não és voluntário? Candidata-te ")
                    withStyle(style = SpanStyle(color = Color.Blue)) {
                        append("aqui!")
                    }
                }

                ClickableText(
                    text = annotatedString,
                    onClick = { offset ->
                        // mudanca de ecra
                    }
                )
            }
        }

    }
}