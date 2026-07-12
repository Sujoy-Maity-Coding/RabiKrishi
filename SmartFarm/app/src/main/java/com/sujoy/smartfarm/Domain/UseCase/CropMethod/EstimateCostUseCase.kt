package com.sujoy.smartfarm.Domain.UseCase.CropMethod


import com.sujoy.smartfarm.AI.model.EstimateCostRequest
import com.sujoy.smartfarm.Domain.model.CropMethod.CostEstimation
import com.sujoy.smartfarm.Domain.repo.GeminiRepository
import javax.inject.Inject

class EstimateCostUseCase @Inject constructor(

    private val repository: GeminiRepository

) {

    suspend operator fun invoke(

        request: EstimateCostRequest

    ): Result<CostEstimation> {

        return repository.estimateCost(request)

    }

}