package com.sujoy.smartfarm.Presentation.State

import com.sujoy.smartfarm.Domain.model.CropSchedule

data class CropScheduleState(

    val isLoading: Boolean = false,

    val schedule: CropSchedule? = null,

    val error: String = ""

)