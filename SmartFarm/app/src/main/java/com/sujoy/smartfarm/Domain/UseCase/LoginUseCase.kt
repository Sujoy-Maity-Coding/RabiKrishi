package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class LoginUseCase @Inject constructor(

    private val repo: Repo

) {

    fun login(

        email: String,

        password: String

    ) = repo.login(
        email,
        password
    )
}