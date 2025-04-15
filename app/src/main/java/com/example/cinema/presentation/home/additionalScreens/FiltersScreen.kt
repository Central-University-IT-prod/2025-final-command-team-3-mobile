package com.example.cinema.presentation.home.additionalScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FiltersScreen(
    onApplyFilters: (Int, Int, Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    // Состояния для слайдеров
    var yearFrom by remember { mutableStateOf(1900) }
    var yearTo by remember { mutableStateOf(2023) }
    var ratingFrom by remember { mutableStateOf(0.0) }
    var ratingTo by remember { mutableStateOf(10.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок экрана
        Text(
            text = "Фильтры",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Слайдер для фильтрации по году выпуска
        Text(
            text = "Год выпуска: от $yearFrom до $yearTo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        RangeSlider(
            value = yearFrom.toFloat()..yearTo.toFloat(),
            onValueChange = { range ->
                yearFrom = range.start.toInt()
                yearTo = range.endInclusive.toInt()
            },
            valueRange = 1900f..2023f,
            steps = 123,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Слайдер для фильтрации по рейтингу
        Text(
            text = "Рейтинг: от ${"%.1f".format(ratingFrom)} до ${"%.1f".format(ratingTo)}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        RangeSlider(
            value = ratingFrom.toFloat()..ratingTo.toFloat(),
            onValueChange = { range ->
                ratingFrom = range.start.toDouble()
                ratingTo = range.endInclusive.toDouble()
            },
            valueRange = 0f..10f,
            steps = 100,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Кнопка для применения фильтров
        Button(
            onClick = {
                onApplyFilters(yearFrom, yearTo, ratingFrom, ratingTo)
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Применить фильтры")
        }

        // Кнопка для отмены и возврата на предыдущий экран
        TextButton(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Отмена")
        }
    }
}