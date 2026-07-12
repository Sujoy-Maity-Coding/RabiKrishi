package com.sujoy.smartfarm.Domain.UseCase.AnalyzeCrop

import com.sujoy.smartfarm.AI.model.GeminiRequest
import com.sujoy.smartfarm.Domain.repo.GeminiRepository
import javax.inject.Inject

class AnalyzeCropUseCase @Inject constructor(

    private val repository: GeminiRepository

) {

    suspend operator fun invoke(

        request: GeminiRequest

    ) = repository.analyzeCrop(request)

}