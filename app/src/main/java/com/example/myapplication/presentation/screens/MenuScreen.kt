package com.example.myapplication.presentation.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.domain.utils.ImageUtils
import com.example.myapplication.presentation.viewModels.MenuViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.io.File

@Composable
fun MenuScreen(navController: NavHostController) {
    val mainActivity = LocalContext.current as MainActivity
    val menuViewModel: MenuViewModel = viewModel()
    val user = Firebase.auth.currentUser

    user?.let {
        LaunchedEffect(Unit) {
            menuViewModel.getCurrentUser(mainActivity, user.uid)
            menuViewModel.getFuncionalidades(mainActivity)
            //menuViewModel.getAllData()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
            if (menuViewModel.userData != null) {
                TopSection(navController, menuViewModel, mainActivity)

                when(menuViewModel.userData!!.state){
                    1 -> GridConstructor(navController, menuViewModel, itemsPerRow = 2)
                    0, 2 -> NotActiveConstructor()
                }
            }
        }
    }
}

@Composable
fun DefaultUserImage(navController: NavHostController, uid: String) {
    Image(
        painter = painterResource(id = R.drawable.profile_image_placeholder),
        contentDescription = "Profile Image",
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.Gray)
            .clickable(onClick = {
                navController.navigate("user_settings/${uid}")
            }),
    )
}

@Composable
fun TopSection(navController: NavHostController, menuViewModel: MenuViewModel, context: Context) {
    val filePath = context.filesDir.absolutePath + "/profile.jpg"
    if (menuViewModel.userData!!.photo != "") {
        ImageUtils.saveImageFromBase64(menuViewModel.userData!!.photo, filePath)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF1B6089))
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (menuViewModel.userData!!.photo != ""){
                val file = File(filePath)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(filePath)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Imagem de Perfil",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .clickable(onClick = {
                                navController.navigate("user_settings/${menuViewModel.userData!!.id}")
                            }),
                    )
                } else {
                    DefaultUserImage(navController, menuViewModel.userData!!.id)
                }
            } else {
                DefaultUserImage(navController, menuViewModel.userData!!.id)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Bem vind@, ${menuViewModel.userData!!.name}",
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
    image: ImageVector,
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
                imageVector = image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .aspectRatio(1f),
                tint = Color(0xFF434343)
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

@Composable
fun NotActiveConstructor() {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Conta encerrada ou candidatura pendente.",
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}