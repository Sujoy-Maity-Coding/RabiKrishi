package com.sujoy.smartfarm.Presentation.State

data class AuthState(

    val isLoading: Boolean = false,

    val success: String = "",

    val error: String = ""

)