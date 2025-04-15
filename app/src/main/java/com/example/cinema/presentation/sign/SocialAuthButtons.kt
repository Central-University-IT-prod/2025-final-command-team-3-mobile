package com.example.cinema.presentation.sign

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cinema.R

@Composable
fun SocialAuthButtons(
    onYandexClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.yandex_logo),
            contentDescription = "yandex_logo",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onYandexClick() }
        )

        Spacer(Modifier.height(8.dp))
        }
}