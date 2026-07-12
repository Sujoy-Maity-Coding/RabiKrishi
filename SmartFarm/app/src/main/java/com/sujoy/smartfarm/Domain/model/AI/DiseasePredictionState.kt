package com.sujoy.smartfarm.Domain.model.AI

data class DiseasePredictionState(

    val isLoading:Boolean=false,

    val prediction:DiseasePredictionResponse?=null,

    val error:String=""

)