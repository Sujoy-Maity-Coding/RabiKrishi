package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.Domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateFarmerProfileUseCase @Inject constructor(

    private val repo: Repo

) {

    operator fun invoke(

        name: String,

        phoneNumber: String

    ): Flow<ResultState<String>> {

        return repo.updateFarmerProfile(name, phoneNumber)
    }
}