package com.sujoy.smartfarm.Presentation.State

data class SaveDailyUpdateState(

    val isLoading: Boolean = false,

    val success: String = "",

    val error: String = ""
)