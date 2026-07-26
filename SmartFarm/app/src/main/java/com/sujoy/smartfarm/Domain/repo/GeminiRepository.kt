package com.sujoy.smartfarm.Domain.repo

import com.sujoy.smartfarm.AI.model.CropRecommendationRequest
import com.sujoy.smartfarm.AI.model.EstimateCostRequest
import com.sujoy.smartfarm.AI.model.GeminiAnalysisResult
import com.sujoy.smartfarm.AI.model.GeminiRequest
import com.sujoy.smartfarm.AI.model.ScheduleGenerationRequest
import com.sujoy.smartfarm.Domain.model.Crop
import com.sujoy.smartfarm.Domain.model.CropMethod.CostEstimation
import com.sujoy.smartfarm.Domain.model.CropSchedule
import com.sujoy.smartfarm.Domain.model.Phase

interface GeminiRepository {

    suspend fun analyzeCrop(
        request: GeminiRequest
    ): Result<GeminiAnalysisResult>

    suspend fun estimateCost(

        request: EstimateCostRequest

    ): Result<CostEstimation>

    suspend fun getCropRecommendations(

        request: CropRecommendationRequest

    ): Result<List<Crop>>

    suspend fun generateSchedule(

        request: ScheduleGenerationRequest

    ): Result<Phase>   // ← changed from Result<CropSchedule>

}