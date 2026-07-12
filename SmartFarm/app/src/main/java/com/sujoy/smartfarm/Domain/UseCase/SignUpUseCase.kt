package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Domain.model.FarmerData
import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class SignUpUseCase @Inject constructor(

    private val repo: Repo

) {

    fun signUp(

        farmerData: FarmerData,

        password: String

    ) = repo.signUp(
        farmerData,
        password
    )
}