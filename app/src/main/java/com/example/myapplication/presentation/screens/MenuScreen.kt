package com.example.myapplication.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.R
import com.example.myapplication.presentation.viewModels.MenuViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
// Receber viewmodel também
fun MenuScreen(navController: NavHostController, isGestor: Boolean) {
    val menuViewModel: MenuViewModel = viewModel()
    val user = Firebase.auth.currentUser                // !! Melhorar isto?
    menuViewModel.isGestor = isGestor

    // !!! Arranjar depois de testes
    //user?.let {
        LaunchedEffect(Unit) {
            menuViewModel.getFuncionalidades()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
            TopSection(navController, menuViewModel)

            GridConstructor(navController, menuViewModel, itemsPerRow = 2)
        }
    //}
}

@Composable
fun TopSection(navController: NavHostController, menuViewModel: MenuViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF08639B))
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.profile_image_placeholder),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable(onClick = {
                        //navController.navigate("user_settings/${user.uid}")        // !!! depois abrir com o utilizador
                    }),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Bem vind@, Pessoa Teste",   // !!! depois mostrar o nome do utilizador
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.settings_100dp_cccccc),
            contentDescription = "Settings",
            modifier = Modifier
                .size(56.dp)
                .clickable(onClick = { navController.navigate("settings/${menuViewModel.isGestor}") }),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun GridConstructor(
    navController: NavHostController,
    menuViewModel: MenuViewModel,
    itemsPerRow: Int
) {
    val rows = menuViewModel.listFuncDetail.chunked(itemsPerRow)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(rows) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    VerticalRectangleCard(
                        navController,
                        text = item.name,
                        navigate = item.nav,
                        image = item.img,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size < itemsPerRow) {
                    repeat(itemsPerRow - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun VerticalRectangleCard(
    navController: NavHostController,
    text: String,
    navigate: String,
    image: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .aspectRatio(0.8f)
            .padding(8.dp)
            .clickable {
                navController.navigate(navigate)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .aspectRatio(1f),
                tint = Color.Unspecified
            )
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}