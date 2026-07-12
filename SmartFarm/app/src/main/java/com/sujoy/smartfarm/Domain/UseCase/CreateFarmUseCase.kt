package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Domain.model.Farm
import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class CreateFarmUseCase @Inject constructor(

    private val repo: Repo

){

    operator fun invoke(

        farm: Farm

    ) = repo.createFarm(farm)
}