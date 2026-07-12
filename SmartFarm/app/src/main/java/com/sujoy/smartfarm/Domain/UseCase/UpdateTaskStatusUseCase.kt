package com.sujoy.smartfarm.Domain.UseCase

import com.sujoy.smartfarm.Domain.model.TaskItem
import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class UpdateTaskStatusUseCase @Inject constructor(

    private val repo: Repo

) {

    operator fun invoke(

        farmId: String,

        task: TaskItem,

        completed: Boolean

    ) = repo.updateTaskStatus(

        farmId,

        task,

        completed
    )
}