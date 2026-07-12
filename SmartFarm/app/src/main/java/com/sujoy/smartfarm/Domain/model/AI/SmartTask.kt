package com.sujoy.smartfarm.Domain.model.AI

data class SmartTask(

    val taskId: String = "",

    val title: String,

    val description: String = "",

    val isAI: Boolean = false,

    val canComplete: Boolean = true

)