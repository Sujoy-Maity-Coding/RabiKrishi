package com.sujoy.smartfarm.Presentation.Components.CropMethod

import androidx.compose.ui.graphics.Color


enum class FarmMethod(
    val label: String,
    val emoji: String,
    val tagline: String,
    val color: Color
) {
    ORGANIC   ("Organic",   "🌿", "Natural inputs only",          Color(0xFF2E7D32)),
    INORGANIC ("Inorganic", "🧪", "Chemical fertilizers & sprays", Color(0xFF1565C0)),
    MIXED     ("Mixed",     "⚗️", "Best of both worlds",           Color(0xFFF57F17))
}