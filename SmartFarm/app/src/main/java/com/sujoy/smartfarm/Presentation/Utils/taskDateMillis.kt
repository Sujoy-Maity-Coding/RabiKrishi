package com.sujoy.smartfarm.Presentation.Utils

fun taskDateMillis(

    farmStartDate: Long,

    taskDay: Int,

    scheduleOffsetDays: Int = 0

): Long {

    return farmStartDate +

            (((taskDay - 1L) + scheduleOffsetDays) * 24L * 60L * 60L * 1000L)

}