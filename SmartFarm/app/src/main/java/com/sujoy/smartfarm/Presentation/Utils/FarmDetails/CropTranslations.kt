package com.sujoy.smartfarm.Presentation.Utils.FarmDetails

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sujoy.smartfarm.R

// Add to CropTranslations.kt
@Composable
fun translatedRiskLevel(value: String): String {
    return when (value.trim().lowercase()) {
        "healthy" -> stringResource(R.string.risk_healthy)
        "low" -> stringResource(R.string.risk_low)
        "medium" -> stringResource(R.string.risk_medium)
        "high" -> stringResource(R.string.risk_high)
        "critical" -> stringResource(R.string.risk_critical)
        else -> value
    }
}

@Composable
fun translatedSeverity(value: String): String {
    return when (value.trim().lowercase()) {
        "mild" -> stringResource(R.string.severity_mild)
        "moderate" -> stringResource(R.string.severity_moderate)
        "severe" -> stringResource(R.string.severity_severe)
        else -> value
    }
}

@Composable
fun translatedLeafColor(value: String): String = when (value.trim()) {
    "Dark Green" -> stringResource(R.string.leaf_dark_green)
    "Green" -> stringResource(R.string.leaf_green)
    "Light Green" -> stringResource(R.string.leaf_light_green)
    "Yellow" -> stringResource(R.string.leaf_yellow)
    "Brown" -> stringResource(R.string.leaf_brown)
    "Spotted" -> stringResource(R.string.leaf_spotted)
    "Dry" -> stringResource(R.string.leaf_dry)
    else -> value
}

@Composable
fun translatedSoilMoisture(value: String): String = when (value.trim()) {
    "Very Dry" -> stringResource(R.string.moisture_very_dry)
    "Dry" -> stringResource(R.string.moisture_dry)
    "Medium" -> stringResource(R.string.moisture_medium)
    "Wet" -> stringResource(R.string.moisture_wet)
    "Waterlogged" -> stringResource(R.string.moisture_waterlogged)
    else -> value
}