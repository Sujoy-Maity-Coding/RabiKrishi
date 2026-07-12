package com.sujoy.smartfarm.Presentation.State

import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate

data class CropUpdateDetailsState(

    val isLoading:Boolean=false,

    val update: DailyFarmUpdate?=null,

    val error:String=""
)