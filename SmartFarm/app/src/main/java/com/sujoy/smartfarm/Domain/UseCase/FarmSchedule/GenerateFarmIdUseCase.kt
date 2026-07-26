package com.sujoy.smartfarm.Domain.UseCase.FarmSchedule

import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class GenerateFarmIdUseCase @Inject constructor(
    private val repo: Repo
) {
    operator fun invoke(): String = repo.generateFarmId()
}