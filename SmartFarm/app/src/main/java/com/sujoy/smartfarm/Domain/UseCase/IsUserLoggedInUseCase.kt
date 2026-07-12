package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(

    private val repo: Repo

) {

    operator fun invoke(): Boolean {

        return repo.isUserLoggedIn()
    }
}