package com.sujoy.smartfarm.AI.scheduler

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkScheduler {

    fun scheduleTodayTaskWorker(
        context: Context
    ) {

        val now = Calendar.getInstance()

        val target = Calendar.getInstance().apply {
//            add(Calendar.MINUTE, 1)

            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)

            if (before(now)) {

                add(Calendar.DAY_OF_MONTH, 1)

            }

        }

        val initialDelay =

            target.timeInMillis -
                    now.timeInMillis

        val workRequest =

            PeriodicWorkRequestBuilder<TodayTaskWorker>(

                24,
                TimeUnit.HOURS

            )

                .setInitialDelay(

                    initialDelay,

                    TimeUnit.MILLISECONDS

                )

                .build()

        WorkManager.getInstance(context)

            .enqueueUniquePeriodicWork(

                "today_task_worker",

                ExistingPeriodicWorkPolicy.UPDATE,

                workRequest

            )

    }

}