package com.sujoy.smartfarm.Domain.UseCase.CropMethod

import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class GetCropMethodUseCase @Inject constructor(
    private val repo: Repo
) {

    operator fun invoke(
        cropId: String, languageCode: String
    ) = repo.getCropMethod(cropId, languageCode)
}