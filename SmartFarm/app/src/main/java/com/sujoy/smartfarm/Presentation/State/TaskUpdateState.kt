package com.sujoy.smartfarm.Presentation.State

data class TaskUpdateState(

    val isLoading: Boolean = false,

    val success: String = "",

    val error: String = ""

)