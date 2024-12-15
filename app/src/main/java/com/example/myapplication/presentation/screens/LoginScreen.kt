package com.example.myapplication.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.presentation.viewModels.LoginViewModel

@Composable
fun LoginScreen(navController: NavHostController, loginViewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val mainActivity = LocalContext.current as MainActivity

    val email = loginViewModel.email.value
    val password = loginViewModel.password.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 32.dp)
                .height(150.dp),
            painter = painterResource(id = R.drawable.lojalogo),
            contentDescription = "O logo da loja social"
        )

        // Email TextField
        TextField(
            value = email,
            onValueChange = loginViewModel::updateEmail,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .background(Color.White, shape = RoundedCornerShape(30.dp)),
            placeholder = { Text("Email") },
            label = { Text("Email") }
        )

        // Password TextField
        TextField(
            value = password,
            onValueChange = loginViewModel::updatePassword,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .background(Color.White, shape = RoundedCornerShape(30.dp)),
            placeholder = { Text("Password") },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        // Login Button
        Button(
            onClick = {
                loginViewModel.loginUser(
                    navController,
                    mainActivity
                )
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(0.5f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC1F26))
        ) {
            Text(text = "Login", color = Color.White)
        }

        // Registration Text
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
                    pushStringAnnotation(tag = "register_link", annotation = "register")
                    withStyle(style = SpanStyle(color = Color.Blue)) {
                        append("aqui!")
                    }
                    pop()
                }

                Text(
                    text = annotatedString,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("candidatura")
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}