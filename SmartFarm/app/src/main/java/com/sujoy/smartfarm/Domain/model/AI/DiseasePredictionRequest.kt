package com.sujoy.smartfarm.Domain.model.AI

data class DiseasePredictionRequest(

    val imageUrl: String,

    val plantHeight: Double,

    val leafColor: String,

    val soilMoisture: String,

    val pestFound: Boolean,

    val diseaseFound: Boolean,

    val floweringStarted: Boolean,

    val fruitStarted: Boolean,

    val farmerNote: String
)