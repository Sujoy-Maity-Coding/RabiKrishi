package com.sujoy.smartfarm.Domain.model.AI

data class AIResult(

    val diseaseName: String = "",

    val farmingMethod: String = "",

    val confidence: Int = 0,

    val severity: String = "",

    val healthScore: Int = 100,

    val riskLevel: String = "",

    val organicTreatment: String = "",

    val chemicalTreatment: String = "",

    val recommendedMedicine: String = "",

    val medicineQuantity: String = "",

    val irrigationAdvice: String = "",

    val fertilizerAdvice: String = "",

    val todayTasks: List<String> = emptyList(),

    val preventiveTips: List<String> = emptyList(),

    val recommendation: List<String> = emptyList(),

    val postponeTasks: List<String> = emptyList(),

    val extraTasks: List<String> = emptyList(),

    val cancelTasks: List<String> = emptyList(),

    val priority: String = "NORMAL",

    val nextInspectionDays: Int = 0,

    val aiModel: String = "gemini-2.5-flash",

    val predictedAt: Long = System.currentTimeMillis(),

    val currentPhase: String = "",

    val progress: Int = 0

)