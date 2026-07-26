package com.sujoy.smartfarm.Presentation.Components.Crop

import androidx.compose.ui.graphics.Color
import com.sujoy.smartfarm.Domain.model.Crop
import com.sujoy.smartfarm.ui.theme.GreenPrimary

data class CropMeta(

    val emoji: String,

    val accentColor: Color

)

private val cropMetaMap = mapOf(

    "Rice" to CropMeta("🌾", Color(0xFF2E7D32)),

    "Paddy" to CropMeta("🌾", Color(0xFF2E7D32)),

    "Jute" to CropMeta("🪢", Color(0xFF6D4C41)),

    "Potato" to CropMeta("🥔", Color(0xFFF57F17)),

    "Mustard" to CropMeta("🌻", Color(0xFFF9A825)),

    "Wheat" to CropMeta("🌾", Color(0xFFEF8C00)),

    "Maize" to CropMeta("🌽", Color(0xFFFFB300)),

    "Sunflower" to CropMeta("🌻", Color(0xFFFBC02D)),

    "Groundnut" to CropMeta("🥜", Color(0xFF8D6E63)),

    "Sugarcane" to CropMeta("🎋", Color(0xFF43A047)),

    "Tomato" to CropMeta("🍅", Color(0xFFE53935)),

    "Brinjal" to CropMeta("🍆", Color(0xFF7B1FA2)),

    "Onion" to CropMeta("🧅", Color(0xFF8D6E63)),

    "Cabbage" to CropMeta("🥬", Color(0xFF66BB6A)),

    "Cauliflower" to CropMeta("🥦", Color(0xFF9CCC65)),

    "Chilli" to CropMeta("🌶️", Color(0xFFD32F2F)),

    "Boro" to CropMeta("🌾", Color(0xFF1B5E20)),

    "Aman" to CropMeta("🌾", Color(0xFF2E7D32)),

    "Aus" to CropMeta("🌾", Color(0xFF388E3C))

)

fun metaFor(crop: Crop): CropMeta {

    return cropMetaMap.entries

        .firstOrNull {

            crop.cropName.contains(

                it.key,

                ignoreCase = true

            )

        }?.value

        ?: CropMeta(

            emoji = "🌱",

            accentColor = GreenPrimary

        )

}