package com.sujoy.smartfarm.Domain.model.CropMethod

data class CostEstimation(

    val estimatedCost: String = "",

    val estimatedYield: String = "",

    val estimatedDuration: String = "",

    val labourRequired: String = "",

    val materials: List<MaterialItem> = emptyList(),

    val notes: String = "",
    val farmSize: Double = 0.0

)