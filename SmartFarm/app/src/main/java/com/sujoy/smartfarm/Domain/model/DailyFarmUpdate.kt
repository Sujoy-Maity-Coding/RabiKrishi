package com.sujoy.smartfarm.Domain.model

import com.sujoy.smartfarm.Domain.model.AI.AIResult

data class DailyFarmUpdate(

    val updateId: String = "",

    val day: Int = 0,

    val date: Long = 0L,

    val plantHeight: Double = 0.0,

    val leafColor: String = "",

    val soilMoisture: String = "",

    val pestFound: Boolean = false,

    val diseaseFound: Boolean = false,

    val floweringStarted: Boolean = false,

    val fruitStarted: Boolean = false,

    val farmerNote: String = "",

    val imageUrl: String = "",

    val aiResult: AIResult = AIResult(),

    var recommendationCompleted: Boolean = false,

    var recommendationCompletedAt: Long = 0L,

    var nextInspectionDate: Long = 0L
)