package com.sujoy.smartfarm.AI.model

data class CropRecommendationRequest(

    val district: String,

    val month: Int,

    val season: String,

    val soilType: String

)