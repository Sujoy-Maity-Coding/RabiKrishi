package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.Domain.model.FarmerData
import com.sujoy.smartfarm.Domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFarmerProfileUseCase @Inject constructor(

    private val repo: Repo

) {

    operator fun invoke(): Flow<ResultState<FarmerData>> {

        return repo.getFarmerProfile()
    }
}