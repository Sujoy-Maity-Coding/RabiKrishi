package com.sujoy.smartfarm.Domain.model.CropMethod

data class CropMethod(

    val cropId: String = "",

    val organicCost: String = "",
    val organicYield: String = "",

    val organicAdvantages: List<String> = emptyList(),
    val organicDisadvantages: List<String> = emptyList(),

    val inorganicCost: String = "",
    val inorganicYield: String = "",

    val inorganicAdvantages: List<String> = emptyList(),
    val inorganicDisadvantages: List<String> = emptyList(),

    val mixedCost: String = "",
    val mixedYield: String = "",

    val mixedAdvantages: List<String> = emptyList(),
    val mixedDisadvantages: List<String> = emptyList()
)