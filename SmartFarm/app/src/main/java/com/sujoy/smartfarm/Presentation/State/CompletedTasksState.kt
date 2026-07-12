package com.sujoy.smartfarm.Presentation.State

import com.sujoy.smartfarm.Domain.model.CompletedTask

data class CompletedTasksState(

    val isLoading: Boolean = false,

    val completedTasks: List<CompletedTask> = emptyList(),

    val error: String = ""

)