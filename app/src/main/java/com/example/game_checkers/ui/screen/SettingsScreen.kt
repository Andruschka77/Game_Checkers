package com.example.game_checkers.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.items
import com.example.game_checkers.R

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    currentCheckerStyle: CheckerStyle,
    onThemeChanged: (Boolean) -> Unit,
    onCheckerStyleChanged: (CheckerStyle) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isDarkTheme) Color.White else Color.Black
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Выбор темы приложения:",
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { onThemeChanged(false) },
                modifier = Modifier
                    .height(48.dp)
                    .border(
                        width = if (!isDarkTheme) 2.dp else 0.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(50)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Светлая тема")
            }

            Spacer(modifier = Modifier.width(32.dp))

            Button(
                onClick = { onThemeChanged(true) },
                modifier = Modifier
                    .height(48.dp)
                    .border(
                        width = if (isDarkTheme) 2.dp else 0.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(50)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Тёмная тема")
            }
        }

        Text(
            text = "Выбор стиля шашек:",
            style = MaterialTheme.typography.titleLarge
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(CheckerStyle.entries.toTypedArray()) { style ->
                CheckerStyleOption(
                    style = style,
                    isSelected = style == currentCheckerStyle,
                    onClick = { onCheckerStyleChanged(style) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBackPressed,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Назад")
        }
    }
}

@Composable
private fun CheckerStyleOption(
    style: CheckerStyle,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = style.previewResId),
            contentDescription = "Стиль ${style.name}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

enum class CheckerStyle(
    @DrawableRes val previewResId: Int,
    val darkColor: Color,
    val lightColor: Color,
    val darkName: String,
    val lightName: String
) {
    CLASSIC(
        previewResId = R.drawable.checker_classic,
        darkColor = Color.Black,
        lightColor = Color.White,
        darkName = "Чёрные",
        lightName = "Белые"
    ),
    GREEN_YELLOW(
        previewResId = R.drawable.checker_green_yellow,
        darkColor = Color(0xFF388E3C),
        lightColor = Color(0xFFFFEB3B),
        darkName = "Зелёные",
        lightName = "Жёлтые"
    ),
    BLUE_RED(
        previewResId = R.drawable.checker_blue_red,
        darkColor = Color(0xFF1976D2),
        lightColor = Color(0xFFD32F2F),
        darkName = "Синие",
        lightName = "Красные"
    ),
    BROWN_BEIGE(
        previewResId = R.drawable.checker_brown_beige,
        darkColor = Color(0xFF5D4037),
        lightColor = Color(0xFFD7CCC8),
        darkName = "Коричневые",
        lightName = "Бежевые"
    )
}