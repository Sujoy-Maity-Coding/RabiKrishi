package com.sujoy.smartfarm.Presentation.State

import com.sujoy.smartfarm.Domain.model.Farm

data class FarmDetailsState(

    val isLoading: Boolean = false,

    val farm: Farm? = null,

    val error: String = ""

)