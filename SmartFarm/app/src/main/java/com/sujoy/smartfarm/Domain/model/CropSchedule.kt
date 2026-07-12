package com.sujoy.smartfarm.Domain.model

data class CropSchedule(

    val cropId: String = "",

    val phases: List<Phase> = emptyList()
)