package com.sujoy.smartfarm.Data.Repo

import android.content.Context
import com.sujoy.smartfarm.AI.gemini.CropRecommendationParser
import com.sujoy.smartfarm.AI.gemini.EstimateCostParser
import com.sujoy.smartfarm.AI.gemini.GeminiResponseParser
import com.sujoy.smartfarm.AI.gemini.GeminiService
import com.sujoy.smartfarm.AI.gemini.ScheduleGenerationParser
import com.sujoy.smartfarm.AI.model.CropRecommendationRequest
import com.sujoy.smartfarm.AI.model.EstimateCostRequest
import com.sujoy.smartfarm.AI.model.GeminiAnalysisResult
import com.sujoy.smartfarm.AI.model.GeminiRequest
import com.sujoy.smartfarm.AI.model.ScheduleGenerationRequest
import com.sujoy.smartfarm.Domain.model.Crop
import com.sujoy.smartfarm.Domain.model.CropMethod.CostEstimation
import com.sujoy.smartfarm.Domain.model.CropSchedule
import com.sujoy.smartfarm.Domain.model.Phase
import com.sujoy.smartfarm.Domain.repo.GeminiRepository
import com.sujoy.smartfarm.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GeminiRepositoryImpl @Inject constructor(

    private val geminiService: GeminiService,

    private val parser: GeminiResponseParser,

    private val estimateCostParser: EstimateCostParser,

    private val cropRecommendationParser: CropRecommendationParser,

    private val scheduleGenerationParser: ScheduleGenerationParser,

    @ApplicationContext private val context: Context

) : GeminiRepository {

    override suspend fun analyzeCrop(
        request: GeminiRequest
    ): Result<GeminiAnalysisResult> {

        return try {

            val response = geminiService.analyzeCrop(request)

            if (
                response.contains("\"error\"") ||
                response.contains("\"status\"") ||
                response.contains("UNAVAILABLE") ||
                response.contains("503")
            ) {

                return Result.failure(

                    Exception(
                        context.getString(R.string.gemini_err_unavailable_retry)
                    )

                )

            }

            val result = parser.parse(response)

            Result.success(result)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }

    override suspend fun estimateCost(

        request: EstimateCostRequest

    ): Result<CostEstimation> {

        return try {

            val response = geminiService.estimateCost(request)

            if (
                response.contains("\"error\"") ||
                response.contains("\"status\"") ||
                response.contains("503")
            ) {

                return Result.failure(
                    Exception(context.getString(R.string.gemini_err_busy))
                )

            }

            Result.success(

                estimateCostParser.parse(response)

            )

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

    override suspend fun getCropRecommendations(

        request: CropRecommendationRequest

    ): Result<List<Crop>> {

        return try {

            val response =

                geminiService.recommendCrops(request)

            if (

                response.contains("\"error\"") ||

                response.contains("\"status\"") ||

                response.contains("503") ||

                response.contains("UNAVAILABLE")

            ) {

                return Result.failure(

                    Exception(context.getString(R.string.gemini_err_unavailable))

                )

            }

            val recommendations =

                cropRecommendationParser.parse(response)

            if (recommendations.isEmpty()) {

                return Result.failure(

                    Exception(context.getString(R.string.gemini_err_unable_recommendations))

                )

            }

            Result.success(recommendations)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

    override suspend fun generateSchedule(

        request: ScheduleGenerationRequest

    ): Result<Phase> {

        return try {

            val response = geminiService.generateSchedule(request)

            if (
                response.contains("\"error\"") ||
                response.contains("\"status\"") ||
                response.contains("503") ||
                response.contains("UNAVAILABLE")
            ) {

                return Result.failure(
                    Exception(context.getString(R.string.gemini_err_unavailable))
                )

            }

            val phase = scheduleGenerationParser.parse(response)

            if (phase == null) {

                return Result.failure(
                    Exception(context.getString(R.string.gemini_err_unable_phase))
                )

            }

            Result.success(phase)

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

}