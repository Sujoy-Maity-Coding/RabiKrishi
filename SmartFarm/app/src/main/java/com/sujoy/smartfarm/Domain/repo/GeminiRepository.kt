package com.sujoy.smartfarm.Domain.repo

import com.sujoy.smartfarm.AI.model.EstimateCostRequest
import com.sujoy.smartfarm.AI.model.GeminiAnalysisResult
import com.sujoy.smartfarm.AI.model.GeminiRequest
import com.sujoy.smartfarm.Domain.model.CropMethod.CostEstimation

interface GeminiRepository {

    suspend fun analyzeCrop(
        request: GeminiRequest
    ): Result<GeminiAnalysisResult>

    suspend fun estimateCost(

        request: EstimateCostRequest

    ): Result<CostEstimation>

}