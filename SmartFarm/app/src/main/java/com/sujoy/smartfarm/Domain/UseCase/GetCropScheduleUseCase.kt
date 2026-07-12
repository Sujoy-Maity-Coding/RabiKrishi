package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class GetCropScheduleUseCase @Inject constructor(

    private val repo: Repo

) {

    operator fun invoke(

        cropId: String

    ) = repo.getCropSchedule(
        cropId
    )
}