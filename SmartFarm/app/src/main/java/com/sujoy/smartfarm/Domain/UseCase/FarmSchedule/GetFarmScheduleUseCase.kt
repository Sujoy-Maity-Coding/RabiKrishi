package com.sujoy.smartfarm.Domain.UseCase.FarmSchedule

import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.Domain.model.CropSchedule
import com.sujoy.smartfarm.Domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFarmScheduleUseCase @Inject constructor(
    private val repo: Repo
) {
    operator fun invoke(farmId: String): Flow<ResultState<CropSchedule>> {
        return repo.getFarmScheduleFlow(farmId)
    }
}