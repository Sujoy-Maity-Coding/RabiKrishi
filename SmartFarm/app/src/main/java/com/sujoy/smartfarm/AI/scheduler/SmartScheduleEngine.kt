package com.sujoy.smartfarm.AI.scheduler

import com.sujoy.smartfarm.Domain.model.AI.AIResult
import com.sujoy.smartfarm.Domain.model.AI.SmartTask
import com.sujoy.smartfarm.Domain.model.TaskItem

object SmartScheduleEngine {

    fun generateTodayTasks(

        plannedTasks: List<TaskItem>,

        aiResult: AIResult?

    ): List<SmartTask> {

        val smartTasks = mutableListOf<SmartTask>()

        // Static Schedule Tasks
        plannedTasks.forEach { task ->

            if (
                aiResult?.cancelTasks
                    ?.contains(task.title) != true
            ) {

                smartTasks.add(

                    SmartTask(

                        taskId = task.taskId,

                        title = task.title,

                        description = task.description,

                        isAI = false,

                        canComplete = true

                    )

                )

            }

        }

        // AI Today's Tasks
        aiResult?.todayTasks?.forEachIndexed { index, task ->

            smartTasks.add(

                SmartTask(

                    taskId = "ai_today_${task.hashCode()}",

                    title = task,

                    isAI = true,

                    canComplete = true

                )

            )

        }

        // AI Extra Tasks
        aiResult?.extraTasks?.forEachIndexed { index, task ->

            smartTasks.add(

                SmartTask(

                    taskId = "ai_extra_${task.hashCode()}",

                    title = task,

                    isAI = true,

                    canComplete = true

                )

            )

        }

        return smartTasks.distinctBy { it.title }

    }

}