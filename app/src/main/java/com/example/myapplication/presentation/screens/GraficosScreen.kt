package com.example.myapplication.presentation.screens

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.presentation.viewModels.GraficosViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun GraficosScreen(navController: NavHostController) {
    val viewModel: GraficosViewModel = viewModel()
    val pieChartData by viewModel.pieChartData.collectAsState()
    val barGraphData by viewModel.barGraphData.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.fetchCountryVisitData()
        viewModel.fetchMonthlyVisitData()
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
                    text = "Gráficos",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (pieChartData.isNotEmpty()) {
                Text("Visitas por País:", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    PieChart(
                        data = pieChartData,
                        colors = listOf(
                            Color(0xFFD1CA98),
                            Color(0xFF15616D),
                            Color(0xFFDA344D),
                            Color(0xFFFF7D00),
                            Color(0xFF78290F)
                        )
                    )
                }
            }
            if (barGraphData.isNotEmpty()) {
                Text("Visitas por Mês:", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    BarGraph(data = barGraphData)
                }
            }
        }
    }
}

@Composable
fun PieChart(data: Map<String, Float>,colors: List<Color>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val total = data.values.sum()
        var startAngle = -90f

        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 3

        // Para cada país
        data.entries.forEachIndexed { index, entry ->
            val sweepAngle = (entry.value / total) * 360f
            val color = colors[index % colors.size]

            // Desenha a fatia de acordo com o angulo
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )

            // País com número de visitas
            val angleInRadians = (startAngle + sweepAngle / 2) * (PI / 180).toFloat()
            val labelRadius = radius + 20
            val labelX = center.x + labelRadius * cos(angleInRadians)
            val labelY = center.y + labelRadius * sin(angleInRadians)
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    "${entry.key} (${entry.value.toInt()})",
                    labelX,
                    labelY,
                    Paint().apply {
                        textSize = 48f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true

                    }
                )
            }

            startAngle += sweepAngle
        }
    }
}

@Composable
fun BarGraph(data: List<Int>){
    val meses = listOf(
        "Jan.", "Feb.", "Mar.", "Abr.", "Mai.", "Jun.",
        "Jul.", "Ago.", "Set.", "Out.", "Nov.", "Dez."
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val barWidth = size.width / 24  // Espaço para as barras dos meses e padding
        val maxVisits = (data.maxOrNull() ?: 1).toFloat()   // Maior número de visitas
        val barHeightUnit = size.height / maxVisits // Valor da barra mais alta

        // Eixos x e y
        drawLine(
            color = Color.Black,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = 2f
        )

        // Dados
        data.forEachIndexed { index, visits ->
            val left = barWidth * (2 * index + 1)   // Posição horizontal dos dados
            val top = size.height - visits * barHeightUnit  // Topo da barra
            val bottom = size.height    // Fundo da barra

            // Barras
            drawRect(
                color = Color(0xFF1B6089),
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(
                    width = barWidth,
                    height = bottom - top
                )
            )

            // Meses
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    meses[index],
                    left + barWidth / 2,
                    size.height + 40f,
                    Paint().apply {
                        textSize = 36f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }

            // Se tem visitas mostra o número em cima da barra
            if (visits > 0) {
                drawIntoCanvas {
                    it.nativeCanvas.drawText(
                        visits.toString(),
                        left + barWidth / 2,
                        top - 10f,
                        Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 36f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}