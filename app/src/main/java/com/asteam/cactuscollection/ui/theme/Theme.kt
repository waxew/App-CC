package com.asteam.cactuscollection.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CactusColorScheme = lightColorScheme(
    primary = CactusPurple,
    onPrimary = Color.White,
    primaryContainer = CactusLavender,
    onPrimaryContainer = CactusPurpleDark,
    secondary = Color(0xFFE7779E),
    secondaryContainer = CactusPink,
    background = CactusBackground,
    surface = CactusSurface,
    onBackground = CactusText,
    onSurface = CactusText,
    outline = Color(0xFFD7CEDB)
)

@Composable
fun CactusCollectionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CactusColorScheme,
        content = content
    )
}
