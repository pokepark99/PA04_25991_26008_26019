package com.example.myapplication.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.ui.graphics.vector.ImageVector

data class FunctionalityDetail(
    var name: String = "",
    var nav: String = "",
    val img: ImageVector = Icons.Default.Error
)