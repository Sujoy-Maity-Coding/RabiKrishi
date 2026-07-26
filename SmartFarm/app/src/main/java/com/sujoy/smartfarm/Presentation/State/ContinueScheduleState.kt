package com.sujoy.smartfarm.Presentation.State

data class ContinueScheduleState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String = "",
    val isComplete: Boolean = false   // true when no more phases left to generate
)