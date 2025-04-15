package com.example.cinema.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

enum class WatchStatus(val apiValue: String, val russianName: String) {
    DELETE("delete", "Не смотрю"),
    WILL_WATCH("will_watch", "В планах"),
    WATCHED("watched", "Просмотрено"),
    DROPPED("dropped", "Брошено");

    companion object {
        fun fromApiValue(apiValue: String?): WatchStatus? {
            return entries.find { it.apiValue == apiValue }
        }
    }
}

@Composable
fun StatusDialog(
    currentStatus: WatchStatus?,
    onDismissRequest: () -> Unit,
    onStatusSelected: (WatchStatus) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentStatus) }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Заголовок
                Text(
                    text = "Выберите статус просмотра",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Список статусов
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    WatchStatus.entries.forEach { status ->
                        StatusItem(
                            status = status,
                            isSelected = selectedStatus == status,
                            onStatusSelected = {
                                selectedStatus = it
                                onStatusSelected(it)
                            }
                        )
                    }
                }

                // Кнопка "Отмена"
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Отмена")
                }
            }
        }
    }
}

@Composable
private fun StatusItem(
    status: WatchStatus,
    isSelected: Boolean,
    onStatusSelected: (WatchStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStatusSelected(status) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Круглый чекбокс (RadioButton)
        RadioButton(
            selected = isSelected,
            onClick = { onStatusSelected(status) }
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Текст статуса
        Text(
            text = status.russianName,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}