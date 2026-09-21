package com.battuk.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.battuk.app.util.rememberImagePickers

@Composable
fun AvatarPicker(
    photoUri: String?,
    onPhotoChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 96.dp
) {
    var showChooser by remember { mutableStateOf(false) }
    val pickers = rememberImagePickers(onImageSaved = onPhotoChanged)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { showChooser = true },
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(model = photoUri, contentDescription = "Photo", modifier = Modifier.size(size).clip(CircleShape))
        } else {
            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(size * 0.5f))
        }
    }

    if (showChooser) {
        PhotoChooserDialog(
            onDismiss = { showChooser = false },
            onTakePhoto = { showChooser = false; pickers.takePhoto() },
            onPickGallery = { showChooser = false; pickers.pickFromGallery() }
        )
    }
}

@Composable
fun PhotoChooserDialog(
    onDismiss: () -> Unit,
    onTakePhoto: () -> Unit,
    onPickGallery: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a photo") },
        text = {
            Column {
                TextButton(onClick = onTakePhoto) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null)
                    Text("  Take Photo", modifier = Modifier.padding(start = 8.dp))
                }
                TextButton(onClick = onPickGallery) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                    Text("  Choose from Gallery", modifier = Modifier.padding(start = 8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
