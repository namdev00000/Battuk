package com.battuk.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.battuk.app.util.AppThemeMode

/**
 * Standard screen top bar. The theme toggle is always rendered as the right-most
 * action, satisfying the "theme toggle fixed at top-right of the screen" requirement.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattukTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    currentTheme: AppThemeMode,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            IconButton(onClick = onToggleTheme) {
                val icon = when (currentTheme) {
                    AppThemeMode.LIGHT -> Icons.Filled.LightMode
                    AppThemeMode.DARK -> Icons.Filled.DarkMode
                    AppThemeMode.SYSTEM -> Icons.Filled.SettingsBrightness
                }
                Icon(icon, contentDescription = "Toggle theme", modifier = Modifier.size(24.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
    )
}
