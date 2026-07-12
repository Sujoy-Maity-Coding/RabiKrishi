package com.sujoy.smartfarm.Presentation.Components.Crop

import androidx.compose.ui.graphics.Color
import com.sujoy.smartfarm.ui.theme.GreenPrimary

class CropMeta(
    val emoji: String,
    val category: String,
    val matchScore: Int,        // 0–100
    val waterNeed: String,      // Low / Medium / High
    val growthDays: String,
    val marketDemand: String,   // Low / Medium / High
    val accentColor: Color
)

val cropMetaMap: Map<String, CropMeta> = mapOf(
    "Rice"    to CropMeta("🌾","Cereal",96,"High","90–120 days","Very High", Color(0xFF2E7D32)),
    "Paddy"   to CropMeta("🌾","Cereal",94,"High","90–120 days","Very High", Color(0xFF2E7D32)),
    "Jute"    to CropMeta("🪢","Fibre", 88,"High","120 days","High",        Color(0xFF6D4C41)),
    "Potato"  to CropMeta("🥔","Vegetable",91,"Medium","70–90 days","High", Color(0xFFF57F17)),
    "Mustard" to CropMeta("🌻","Oilseed",85,"Low","90–110 days","High",     Color(0xFFF9A825)),
    "Wheat"   to CropMeta("🌽","Cereal",80,"Medium","110–150 days","High",  Color(0xFFEF8C00)),
    "Maize"   to CropMeta("🌽","Cereal",78,"Medium","80–100 days","Medium", Color(0xFFFFB300)),
    "Boro"    to CropMeta("🌾","Cereal",90,"High","130–150 days","High",    Color(0xFF1B5E20)),
    "Aman"    to CropMeta("🌾","Cereal",92,"High","130–150 days","High",    Color(0xFF2E7D32)),
    "Aus"     to CropMeta("🌾","Cereal",75,"Medium","80–100 days","Medium", Color(0xFF388E3C)),
)

fun metaFor(cropName: String): CropMeta =
    cropMetaMap.entries.firstOrNull { cropName.contains(it.key, ignoreCase = true) }?.value
        ?: CropMeta("🌱","Crop",70,"Medium","90–120 days","Medium", GreenPrimary)
