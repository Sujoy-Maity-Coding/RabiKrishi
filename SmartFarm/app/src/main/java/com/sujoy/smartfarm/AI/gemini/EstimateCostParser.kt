package com.sujoy.smartfarm.AI.gemini

import com.google.gson.Gson
import com.sujoy.smartfarm.Domain.model.CropMethod.CostEstimation
import javax.inject.Inject

class EstimateCostParser @Inject constructor() {

    private val gson = Gson()

    fun parse(
        response: String
    ): CostEstimation {

        val cleanJson = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return gson.fromJson(
            cleanJson,
            CostEstimation::class.java
        )
    }

}