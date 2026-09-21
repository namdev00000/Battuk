package com.battuk.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

fun categoryIcon(iconKey: String): ImageVector = when (iconKey) {
    "market" -> Icons.Filled.ShoppingCart
    "kirana" -> Icons.Filled.LocalGroceryStore
    "clothes" -> Icons.Filled.Checkroom
    "shoes" -> Icons.Filled.Checkroom
    "toiletries" -> Icons.Filled.CleaningServices
    "milk" -> Icons.Filled.WaterDrop
    "fastfood" -> Icons.Filled.Fastfood
    "entertainment" -> Icons.Filled.LocalMovies
    "loan" -> Icons.Filled.RequestQuote
    "fuel" -> Icons.Filled.LocalGasStation
    else -> Icons.Filled.MoreHoriz
}
