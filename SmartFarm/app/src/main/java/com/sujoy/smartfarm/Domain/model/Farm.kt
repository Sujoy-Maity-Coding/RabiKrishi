package com.sujoy.smartfarm.Domain.model

import com.sujoy.smartfarm.Domain.model.CropMethod.MaterialItem

data class Farm(

    var farmId: String = "",
    var farmName: String = "",

    var cropId: String = "",
    var cropName: String = "",

    var farmingMethod: String = "",

    var landArea: String = "",

    var startDate: Long = 0L,

    var currentScheduleShift: Int = 0,

    var lastShiftTaskDay: Int = 0,

    var lastAnalysisDate: Long = 0L,

    var isCompleted: Boolean = false,

    // AI Planning
    var aiEstimatedFarmSize: Double = 0.0,

    var aiEstimatedCost: String = "",

    var aiEstimatedYield: String = "",

    var aiEstimatedDuration: String = "",

    var aiLabourRequired: String = "",

    var aiNotes: String = ""

)