package com.sujoy.smartfarm.Presentation.Components.FarmList

import androidx.compose.ui.graphics.Color

fun methodColor(method: String): Color = when (method.lowercase()) {
    "organic"   -> Color(0xFF2E7D32)
    "inorganic" -> Color(0xFF1565C0)
    else        -> Color(0xFFF57F17)   // mixed
}

fun methodEmoji(method: String): String = when (method.lowercase()) {
    "organic"   -> "🌿"
    "inorganic" -> "🧪"
    else        -> "⚗️"
}