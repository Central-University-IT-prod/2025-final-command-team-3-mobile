package com.example.cinema.presentation.collections

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinema.domain.model.Film
import com.example.cinema.presentation.home.additionalScreens.ExpandedFilmCard
import com.example.cinema.presentation.home.FilmCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionsScreen(innerPadding: PaddingValues, viewModel: CollectionsViewModel) {
    val modifier = Modifier.padding(innerPadding)
    val scrollState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }

    var selectedFilm by remember { mutableStateOf<Film?>(null) }
    val films by viewModel.films.collectAsState()

    val tabs = listOf("В планах", "Просмотрено", "Брошено")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Маппинг индексов вкладок на статусы
    val statusMap = mapOf(
        0 to "will_watch",
        1 to "watched",
        2 to "dropped"
    )

    // Вызов fetchCollections при изменении вкладки
    LaunchedEffect(selectedTabIndex) {
        val status = statusMap[selectedTabIndex] ?: "will_watch"
        viewModel.getFilmsInCollections(status)
    }

    if (selectedFilm != null) {
        BackHandler {
            selectedFilm = null
        }
        ExpandedFilmCard(
            film = selectedFilm!!,
            onBackClicked = { selectedFilm = null },
            onStatusChanged = { filmId ->
                viewModel.removeFilmFromList(filmId) // Удаляем фильм из текущего списка
            }
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp, 16.dp, 16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = scrollState
                ) {
                    stickyHeader {
                        Column(
                            modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        ) {
                            Text(
                                text = "Мои коллекции",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                            TabRow(
                                selectedTabIndex = selectedTabIndex,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                tabs.forEachIndexed { index, title ->
                                    Tab(
                                        selected = selectedTabIndex == index,
                                        onClick = { selectedTabIndex = index },
                                        text = {
                                            Text(text = title,
                                                color = if(selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Отображение коллекций в зависимости от выбранной вкладки
                    if (films.isNotEmpty()) {
                        items(films) { collection ->
                            FilmCard(
                                film = collection,
                                onClick = { selectedFilm = collection }
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "Ой, здесь пусто! Давайте добавим фильмы?",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}