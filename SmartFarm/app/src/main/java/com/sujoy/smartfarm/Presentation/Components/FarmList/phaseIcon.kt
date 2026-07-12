package com.sujoy.smartfarm.Presentation.Components.FarmList

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

fun phaseIcon(title: String): ImageVector = when {
    title.contains("land",    ignoreCase = true) -> Icons.Outlined.Agriculture
    title.contains("sow",     ignoreCase = true) ||
            title.contains("nursery", ignoreCase = true) -> Icons.Outlined.Grass
    title.contains("growth",  ignoreCase = true) -> Icons.Outlined.Eco
    title.contains("irrig",   ignoreCase = true) -> Icons.Outlined.Water
    title.contains("fertil",  ignoreCase = true) -> Icons.Outlined.Science
    title.contains("pest",    ignoreCase = true) ||
            title.contains("disease", ignoreCase = true) -> Icons.Outlined.BugReport
    title.contains("harvest", ignoreCase = true) -> Icons.Outlined.ContentCut
    title.contains("post",    ignoreCase = true) -> Icons.Outlined.Inventory2
    else                                          -> Icons.Outlined.CalendarToday
}