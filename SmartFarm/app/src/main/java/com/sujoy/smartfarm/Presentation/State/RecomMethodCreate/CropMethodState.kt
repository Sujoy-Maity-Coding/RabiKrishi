package com.sujoy.smartfarm.Presentation.State.RecomMethodCreate

import com.sujoy.smartfarm.Domain.model.CropMethod.CropMethod

data class CropMethodState(

    val isLoading: Boolean = false,

    val cropMethod: CropMethod? = null,

    val error: String = ""
)