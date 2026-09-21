package com.battuk.app.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorage {
    fun saveBitmap(context: Context, bitmap: Bitmap): String {
        val dir = File(context.filesDir, "images").apply { mkdirs() }
        val file = File(dir, "img_${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        return file.toURI().toString()
    }

    fun saveFromUri(context: Context, uri: Uri): String? {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return null
            val dir = File(context.filesDir, "images").apply { mkdirs() }
            val file = File(dir, "img_${UUID.randomUUID()}.jpg")
            input.use { inStream ->
                FileOutputStream(file).use { out -> inStream.copyTo(out) }
            }
            file.toURI().toString()
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Returns a pair of (takePhoto, pickFromGallery) launchers. Call takePhoto() or
 * pickFromGallery() from a click handler; [onImageSaved] fires with the saved
 * app-private file URI string once the image is captured/picked and stored.
 */
@Composable
fun rememberImagePickers(onImageSaved: (String) -> Unit): ImagePickerActions {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            ImageStorage.saveFromUri(context, uri)?.let(onImageSaved)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onImageSaved(ImageStorage.saveBitmap(context, bitmap))
        }
    }

    return remember(galleryLauncher, cameraLauncher) {
        ImagePickerActions(
            takePhoto = { cameraLauncher.launch(null) },
            pickFromGallery = {
                galleryLauncher.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )
    }
}

data class ImagePickerActions(
    val takePhoto: () -> Unit,
    val pickFromGallery: () -> Unit
)
