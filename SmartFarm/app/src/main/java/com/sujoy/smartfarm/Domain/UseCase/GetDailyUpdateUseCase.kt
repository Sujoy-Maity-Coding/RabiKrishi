package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class GetDailyUpdateUseCase @Inject constructor(

    private val repo: Repo

){

    operator fun invoke(

        farmId: String,

        updateId: String

    ) = repo.getDailyUpdate(

        farmId,

        updateId
    )
}