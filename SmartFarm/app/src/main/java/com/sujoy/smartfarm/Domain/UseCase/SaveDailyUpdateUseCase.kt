package com.sujoy.smartfarm.Domain.UseCase

import android.net.Uri
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class SaveDailyUpdateUseCase @Inject constructor(

    private val repo: Repo

) {

    operator fun invoke(

        farmId: String,

        update: DailyFarmUpdate,

        imageUri: Uri?

    ) = repo.saveDailyUpdate(

        farmId,

        update,

        imageUri
    )
}