package com.sujoy.smartfarm.Domain.model

data class CropRecommendationInput(

    val district: String = "",

    val month: String = "",

    val season: String = "",

    val soilType: String = ""

)