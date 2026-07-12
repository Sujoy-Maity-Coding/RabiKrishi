package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class GetRecommendedCropsUseCase
@Inject constructor(

    private val repo: Repo

) {

    operator fun invoke(

        district: String,

        month: Int,

        season: String,

        soilType: String

    ) = repo.getRecommendedCrops(

        district,

        month,

        season,

        soilType
    )
}