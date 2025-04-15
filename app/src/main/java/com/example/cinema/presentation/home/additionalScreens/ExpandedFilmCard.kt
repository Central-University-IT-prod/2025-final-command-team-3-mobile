package com.example.cinema.presentation.home.additionalScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cinema.data.api.FilmApiService
import com.example.cinema.data.api.RetrofitClient
import com.example.cinema.data.local.AuthStorage
import com.example.cinema.data.repository.FilmRepositoryImpl
import com.example.cinema.domain.model.Film
import com.example.cinema.domain.repository.FilmRepository
import com.example.cinema.presentation.home.StatusDialog
import com.example.cinema.presentation.home.WatchStatus
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun formatDate(inputDate: String?): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val date: Date? = inputDate?.let { inputFormat.parse(it) }
    return date?.let { outputFormat.format(it) }
}

@Composable
fun ExpandedFilmCard(
    film: Film,
    onBackClicked: () -> Unit,
    onStatusChanged: (UUID) -> Unit = {},
) {
    val context = LocalContext.current
    val authStorage = remember { AuthStorage(context) }
    val scrollState = rememberScrollState()
    val showStatusDialog = rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val localUiHandler = LocalUriHandler.current

    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .padding(bottom = 72.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = "https://prod-team-3-uad8jq68.REDACTED/api/images${film.posterPath}",
                    contentDescription = "Постер",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .heightIn(200.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop// Закругление угло

                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = film.title ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            lineHeight = 28.sp,
                            modifier = Modifier.padding(end = 8.dp),
                            maxLines = 2
                        )
                        if (!film.originalTitle.isNullOrBlank()) {
                            Text(
                                text = film.originalTitle ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                lineHeight = 18.sp
                            )
                        }

                        if (!film.releaseDate.isNullOrBlank()) {
                            formatDate(film.releaseDate)?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 14.sp,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }

                    if (film.voteAverage != 0.0) {
                        Text(
                            text = "★ %.1f".format(film.voteAverage),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                GenreList(genres = film.genres)

                Spacer(modifier = Modifier.height(4.dp))

                val annotatedText = buildAnnotatedString {
                    val overview = film.overview ?: ""
                    val linkRegex = Regex("https?://[^\\s]+") // Поддерживает http и https
                    val matches = linkRegex.findAll(overview)

                    var lastEnd = 0
                    for (match in matches) {
                        // Добавляем текст до ссылки
                        append(overview.substring(lastEnd, match.range.first))

                        // Добавляем ссылку как аннотацию
                        pushStringAnnotation(
                            tag = "URL",
                            annotation = match.value
                        )
                        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                            append(match.value)
                        }
                        pop()

                        lastEnd = match.range.last + 1
                    }

                    // Добавляем оставшийся текст
                    if (lastEnd < overview.length) {
                        append(overview.substring(lastEnd))
                    }
                }


                var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                // Используем TextLayoutResult для определения позиции клика
                                textLayoutResult?.let { layoutResult ->
                                    val position = layoutResult.getOffsetForPosition(offset)
                                    annotatedText.getStringAnnotations(
                                        tag = "URL",
                                        start = position,
                                        end = position
                                    ).firstOrNull()?.let { annotation ->
                                        localUiHandler.openUri(annotation.item)
                                    }
                                }
                            }
                        },
                    onTextLayout = { layoutResult ->
                        // Сохраняем TextLayoutResult для использования в detectTapGestures
                        textLayoutResult = layoutResult
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.clickable { onBackClicked() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(modifier = Modifier.clickable { showStatusDialog.value = true }) {
                Icon(
                    imageVector = if (film.status.isNullOrEmpty())
                        Icons.Filled.FavoriteBorder
                    else
                        Icons.Filled.Favorite,
                    contentDescription = "Статус просмотра",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp)
                .navigationBarsPadding()
        )
    }

    if (showStatusDialog.value) {
        StatusDialog(
            onDismissRequest = { showStatusDialog.value = false },
            onStatusSelected = { status ->
                showStatusDialog.value = false
                coroutineScope.launch {
                    handleStatusSelection(
                        status = status,
                        film = film,
                        authStorage = authStorage,
                        snackbarHostState = snackbarHostState,
                        onStatusChanged = onStatusChanged
                    )
                }
            },
            currentStatus = film.status?.let { WatchStatus.fromApiValue(it) }
        )
    }
}

private suspend fun handleStatusSelection(
    status: WatchStatus,
    film: Film,
    authStorage: AuthStorage,
    snackbarHostState: SnackbarHostState,
    onStatusChanged: (UUID) -> Unit
) {
    try {
        val retrofit = RetrofitClient.create(authStorage)
        val filmsApiService = retrofit.create(FilmApiService::class.java)
        val filmRepository: FilmRepository = FilmRepositoryImpl(filmsApiService, authStorage)

        if (film.status.isNullOrBlank()) {
            if ("delete".equals(status.apiValue)) return
            filmRepository.addToCollection(film.id, status.apiValue)
        } else if ("delete".equals(status.apiValue)) {
            filmRepository.deleteFilmCollection(film.id)
        } else {
            filmRepository.updateFilmCollection(film.id, status.apiValue)
        }

        onStatusChanged(film.id)
        snackbarHostState.showSnackbar("Добавлено в коллекцию \"${status.russianName}\"")
    } catch (e: Exception) {
        snackbarHostState.showSnackbar("Ошибка сети: ${e.message ?: "Неизвестная ошибка"}")
    }
}

@Composable
private fun GenreList(genres: List<String>?) {
    val updatedGenres = genres?.takeIf { it.isNotEmpty() } ?: listOf("Свой")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(updatedGenres) { genre ->
            GenreChip(name = genre)
        }
    }
}

@Composable
fun GenreChip(name: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}