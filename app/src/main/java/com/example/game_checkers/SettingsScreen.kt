package com.example.game_checkers

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isDarkTheme) Color.White else Color.Black

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Выравнивание по центру
        verticalArrangement = Arrangement.Top
    ) {
        // Заголовок "Выбор темы приложения"
        Text(
            text = "Выбор темы приложения:",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Кнопки выбора темы
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Используем Arrangement.Center
        ) {
            // Кнопка светлой темы
            Button(
                onClick = { onThemeChanged(false) },
                modifier = Modifier
                    .height(48.dp) // Высота кнопки
                    .border(
                        width = if (!isDarkTheme) 2.dp else 0.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(50) // Овальная форма
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Цвет как у системы
                    contentColor = MaterialTheme.colorScheme.onPrimary // Цвет текста как у системы
                ),
                shape = RoundedCornerShape(50) // Овальная форма
            ) {
                Text("Светлая тема")
            }

            Spacer(modifier = Modifier.width(32.dp))

            // Кнопка тёмной темы
            Button(
                onClick = { onThemeChanged(true) },
                modifier = Modifier
                    .height(48.dp) // Высота кнопки
                    .border(
                        width = if (isDarkTheme) 2.dp else 0.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(50) // Овальная форма
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Цвет как у системы
                    contentColor = MaterialTheme.colorScheme.onPrimary // Цвет текста как у системы
                ),
                shape = RoundedCornerShape(50) // Овальная форма
            ) {
                Text("Тёмная тема")
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Отступ между кнопками и текстом

        // Текст "Выбор стиля шашек:"
        Text(
            text = "Выбор стиля шашек:",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка "Назад в меню"
        Button(
            onClick = onBackPressed,
            modifier = Modifier
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary, // Цвет как у системы
                contentColor = MaterialTheme.colorScheme.onPrimary // Цвет текста как у системы
            )
        ) {
            Text("Назад")
        }
    }
}