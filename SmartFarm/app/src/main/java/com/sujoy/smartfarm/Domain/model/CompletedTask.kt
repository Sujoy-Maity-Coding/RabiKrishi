package com.sujoy.smartfarm.Domain.model

data class CompletedTask(

    val taskId: String = "",

    val taskDay: Int = 0,

    val completed: Boolean = false,

    val completedAt: Long = 0L
)