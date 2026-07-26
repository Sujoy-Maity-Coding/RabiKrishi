package com.sujoy.smartfarm.Presentation.State

import com.sujoy.smartfarm.Domain.model.FarmerData

data class ProfileState(

    val isLoading: Boolean = false,

    val farmer: FarmerData? = null,

    val error: String = ""

)