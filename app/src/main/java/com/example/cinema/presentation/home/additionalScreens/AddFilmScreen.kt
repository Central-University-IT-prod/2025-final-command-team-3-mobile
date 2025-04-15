package com.example.cinema.presentation.home.additionalScreens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cinema.presentation.home.HomeViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@Composable
fun AddFilmScreen(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var filmLink by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    val context = LocalContext.current

    val state by viewModel.state.collectAsState()
    val metadata by viewModel.metadata.collectAsState()
    val serverUploadedFilename by viewModel.filename.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it.toString()
            imageFile = File(it.path!!)
        }
    }

    // Состояние для Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isFilmAdded) {
        if (state.isFilmAdded) {
            // Очищаем поля
            title = ""
            description = ""
            filmLink = ""
            imageUri = null
            imageFile = null

            // Показываем Toast
            Toast.makeText(context, "Фильм успешно добавлен", Toast.LENGTH_SHORT).show()

            // Сбрасываем флаг isFilmAdded
            viewModel.resetFilmAddedState()
        }
    }

    LaunchedEffect(imageUri) {
        imageUri?.let { uriString ->
            val uri = Uri.parse(uriString)
            val file = copyFileToCache(context, uri)

            file?.let { copiedFile ->
                val partFile = MultipartBody.Part.createFormData(
                    "file",
                    copiedFile.name,
                    copiedFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
                viewModel.uploadImage(partFile)
            } ?: run {
                Log.e("UploadError", "Не удалось скопировать файл")
            }
        }
    }

    LaunchedEffect(filmLink) {
        if (isValidUrl(filmLink)) {
            viewModel.extractMetadata(filmLink)
        }
    }

    LaunchedEffect(metadata) {
        metadata?.let {
            if (imageUri.isNullOrBlank()) {
                imageUri = it.posterUrl ?: ""
            }
            if (title.isNullOrBlank()) {
                title = it.title ?: ""
            }
            if (description.isNullOrBlank()) {
                description = it.overview ?: ""
            }

            it.posterUrl?.let { posterFileName ->
                viewModel.setFilename(posterFileName)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .clickable { onBack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Добавление фильма",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )

            serverUploadedFilename?.let { filename ->
                AsyncImage(
                    model = "https://prod-team-3-uad8jq68.REDACTED/api/images$filename",
                    contentDescription = "Постер",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            TextField(
                value = filmLink,
                onValueChange = { filmLink = it },
                label = { Text("Ссылка на фильм") },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )

            TextField(
                value = title,
                onValueChange = {
                    if (it.length <= 80) {
                        title = it
                    }},
                label = { Text("Название фильма") },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                enabled = !state.isLoading,
                maxLines = 3,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание фильма") },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState()),
                enabled = !state.isLoading,
                maxLines = 3
            )

            // Анимация загрузки
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Кнопка выбора изображения
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                enabled = !state.isLoading // Блокировка кнопки, если isLoading = true
            ) {
                Text("Выбрать постер")
            }

            // Кнопка добавления
            Button(
                onClick = {
                    if (serverUploadedFilename.isNullOrBlank() || title.isNullOrBlank() || description.isNullOrBlank()) {
                        Toast.makeText(context, "Укажите название, описание и выберите постер.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    serverUploadedFilename?.let {
                        var filmDescription = ""
                        if (!filmLink.isNullOrBlank()) {
                            filmDescription = "Ссылка: " + filmLink + "\n\n" + description
                        } else {
                            filmDescription = description
                        }
                        viewModel.addFilm(title, filmDescription, it, filmLink)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                enabled = !state.isLoading // Блокировка кнопки, если isLoading = true
            ) {
                Text("Добавить фильм")
            }
        }
    }
}

fun isValidUrl(url: String): Boolean {
    return Patterns.WEB_URL.matcher(url).matches()
}

fun copyFileToCache(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "temp_file.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file
    } catch (e: Exception) {
        Log.e("FileCopy", "Ошибка при копировании файла", e)
        null
    }
}

