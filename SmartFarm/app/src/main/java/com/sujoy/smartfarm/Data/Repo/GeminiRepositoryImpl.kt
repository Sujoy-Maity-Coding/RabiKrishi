package com.sujoy.smartfarm.Data.Repo

import com.sujoy.smartfarm.AI.gemini.EstimateCostParser
import com.sujoy.smartfarm.AI.gemini.GeminiResponseParser
import com.sujoy.smartfarm.AI.gemini.GeminiService
import com.sujoy.smartfarm.AI.model.EstimateCostRequest
import com.sujoy.smartfarm.AI.model.GeminiAnalysisResult
import com.sujoy.smartfarm.AI.model.GeminiRequest
import com.sujoy.smartfarm.Domain.model.CropMethod.CostEstimation
import com.sujoy.smartfarm.Domain.repo.GeminiRepository
import javax.inject.Inject

class GeminiRepositoryImpl @Inject constructor(

    private val geminiService: GeminiService,

    private val parser: GeminiResponseParser,

    private val estimateCostParser: EstimateCostParser

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
                        "Gemini AI is temporarily unavailable. Please try again in a few moments."
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
                    Exception("Gemini Busy")
                )

            }

            Result.success(

                estimateCostParser.parse(response)

            )

        } catch (e: Exception) {

            Result.failure(e)

        }

    }

}