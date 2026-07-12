package com.sujoy.smartfarm.Presentation.Utils

import com.sujoy.smartfarm.Domain.model.CompletedTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    private val formatter =
        SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        )

    fun calculateTaskDate(

        farmStartDate: Long,

        taskDay: Int,

        completedTasks: List<CompletedTask>

    ): Long {

        val latestCompleted =

            completedTasks

                .filter {

                    it.completed &&

                            it.taskDay <= taskDay

                }

                .maxByOrNull {

                    it.taskDay

                }

        if (latestCompleted == null) {

            return farmStartDate +

                    (taskDay.toLong() * 24L * 60L * 60L * 1000L)

        }

        val gap =

            taskDay - latestCompleted.taskDay

        return latestCompleted.completedAt +

                (gap.toLong() * 24L * 60L * 60L * 1000L)

    }

    fun calculateTaskDateString(

        farmStartDate: Long,

        taskDay: Int,

        completedTasks: List<CompletedTask>

    ): String {

        return formatter.format(

            Date(

                calculateTaskDate(

                    farmStartDate,

                    taskDay,

                    completedTasks

                )

            )

        )

    }
}