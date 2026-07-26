package com.sujoy.smartfarm.AI.model

import android.net.Uri

data class GeminiRequest(
    val languageCode: String = "en",

    val imageUri: Uri,

    val cropName: String,

    val farmingMethod: String,

    val currentDay: Int,

    val fieldArea: Double,

    val district: String,

    val season: String,

    val plantHeight: Double,

    val leafColor: String,

    val soilMoisture: String,

    val pestFound: Boolean,

    val floweringStarted: Boolean,

    val fruitStarted: Boolean,

    val farmerNote: String

)