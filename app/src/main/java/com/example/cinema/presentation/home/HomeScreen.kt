package com.example.cinema.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cinema.domain.model.Film
import com.example.cinema.presentation.home.additionalScreens.AddFilmScreen
import com.example.cinema.presentation.home.additionalScreens.ExpandedFilmCard
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    viewModel: HomeViewModel
) {
    val modifier = Modifier.padding(innerPadding)
    val state by viewModel.state.collectAsState()
    val films by viewModel.movies.collectAsState()

    val scrollState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    var showAddFilm by remember { mutableStateOf(false) }
    var selectedFilm by remember { mutableStateOf<Film?>(null) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isLoading)

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            viewModel.searchFilms(searchQuery)
        } else {
            viewModel.loadTopFilms()
        }
    }

    when {
        showAddFilm -> {
            AddFilmScreen(
                viewModel = viewModel,
                onBack = { showAddFilm = false }
            )
        }
        selectedFilm != null -> {
            BackHandler { selectedFilm = null }
            ExpandedFilmCard(
                film = selectedFilm!!,
                onBackClicked = { selectedFilm = null }
            )
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { _ ->
                            if (hasFocus) {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        }
                    }
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp, 16.dp, 16.dp)
                ) {
                    SwipeRefresh(
                        state = swipeRefreshState,
                        onRefresh = {
                            if (searchQuery.length >= 3) {
                                viewModel.searchFilms(searchQuery)
                            } else {
                                viewModel.loadTopFilms()
                            }
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            state = scrollState
                        ) {
                            stickyHeader {
                                Column(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Главная",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 40.sp,
                                        )

                                        Row {
                                            Button(
                                                onClick = { showAddFilm = true },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            ) {
                                                Text("Добавить фильм")
                                            }
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() }) {}
                                    ) {
                                        TextField(
                                            value = searchQuery,
                                            onValueChange = { searchQuery = it },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 16.dp)
                                                .focusRequester(focusRequester)
                                                .onFocusChanged { hasFocus = it.isFocused },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.Search,
                                                    contentDescription = null
                                                )
                                            },
                                            placeholder = { Text("Поиск...") },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                focusedIndicatorColor = Color.Transparent,
                                                unfocusedIndicatorColor = Color.Transparent
                                            ),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                            keyboardActions = KeyboardActions(
                                                onSearch = {
                                                    keyboardController?.hide()
                                                }
                                            )
                                        )
                                    }

                                    AnimatedVisibility(
                                        visible = searchQuery.length <= 2,
                                        enter = fadeIn(),
                                        exit = fadeOut()
                                    ) {
                                        Text(
                                            text = "Для вас:",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Start,
                                            fontSize = 24.sp,
                                            modifier = Modifier
                                                .padding(top = 8.dp)
                                                .fillMaxWidth()
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.background,
                                    thickness = 1.dp
                                )
                            }

                            items(films) { film ->
                                FilmCard(
                                    film = film,
                                    onClick = { selectedFilm = film }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorMessage(error: String, onDismiss: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(error) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            onDismiss()
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}