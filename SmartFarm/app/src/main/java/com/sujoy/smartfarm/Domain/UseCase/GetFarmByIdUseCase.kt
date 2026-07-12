package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class GetFarmByIdUseCase @Inject constructor(

    private val repo: Repo

) {

    operator fun invoke(

        farmId: String

    ) = repo.getFarmById(
        farmId
    )
}