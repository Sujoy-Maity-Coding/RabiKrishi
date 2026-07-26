package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.AI.model.CropRecommendationRequest
import com.sujoy.smartfarm.Domain.model.Crop
import com.sujoy.smartfarm.Domain.repo.GeminiRepository
import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class GetCropRecommendationUseCase @Inject constructor(

    private val geminiRepository: GeminiRepository

) {

    suspend operator fun invoke(

        request: CropRecommendationRequest

    ): Result<List<Crop>> {

        return geminiRepository.getCropRecommendations(request)

    }

}