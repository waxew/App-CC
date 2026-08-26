package com.asteam.cactuscollection

import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.runtime.Composable

/**
 * Small package-level wrapper used by the main navigation screen.
 * Keeping it here avoids coupling the rest of the UI file to Material3 imports.
 */
@Composable
internal fun NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    colors: NavigationBarItemColors
) {
    androidx.compose.material3.NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = label,
        colors = colors
    )
}
