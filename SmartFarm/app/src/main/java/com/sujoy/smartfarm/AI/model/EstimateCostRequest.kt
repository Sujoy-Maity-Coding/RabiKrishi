package com.sujoy.smartfarm.AI.model

//data class EstimateCostRequest(
//
//    val cropName: String,
//
//    val farmingMethod: String,
//
//    val farmSize: Double,
//
//    val unit: String = "Acre"
//
//)

data class EstimateCostRequest(
    val cropName: String,
    val farmingMethod: String,
    val farmSize: Double,
    val unit: String = "acres",
    val languageCode: String = "en" // "en" | "bn" | "hi"
)