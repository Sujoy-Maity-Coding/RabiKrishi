package com.sujoy.smartfarm.Presentation.State.RecomMethodCreate

import com.sujoy.smartfarm.Domain.model.Farm

data class MyFarmsState(

    val isLoading: Boolean = false,

    val farms: List<Farm> = emptyList(),

    val error: String = ""

)