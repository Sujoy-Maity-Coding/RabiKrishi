package com.sujoy.smartfarm.AI.model

data class GeminiAnalysisResult(

    val diseaseName: String = "",
    val confidence: Int = 0,
    val severity: String = "",
    val healthScore: Int = 100,
    val currentPhase: String = "",
    val progress: Int = 0,
    val riskLevel: String = "",
    val organicTreatment: String = "",
    val chemicalTreatment: String = "",
    val recommendedMedicine: String = "",
    val medicineQuantity: String = "",
    val irrigationAdvice: String = "",
    val fertilizerAdvice: String = "",
    val todayTasks: List<String> = emptyList(),
    val preventiveTips: List<String> = emptyList(),
    val weatherWarning: String = "",
    val nextInspectionDays: Int = 0,
    val postponeTasks: List<String> = emptyList(),
    val extraTasks: List<String> = emptyList(),
    val cancelTasks: List<String> = emptyList(),
    val priority: String = "NORMAL"
)