package com.sujoy.smartfarm.Domain.model

data class Phase(

    val phaseId: String = "",

    val title: String = "",

    val startDay: Int = 0,

    val endDay: Int = 0,

    val tasks: List<TaskItem> = emptyList()
)