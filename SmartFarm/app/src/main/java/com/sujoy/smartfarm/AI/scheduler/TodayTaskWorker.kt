package com.sujoy.smartfarm.AI.scheduler

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sujoy.smartfarm.Domain.repo.Repo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TodayTaskWorker @AssistedInject constructor(

    @Assisted
    appContext: Context,

    @Assisted
    params: WorkerParameters,

    private val repository: Repo,
    private val notificationEngine: NotificationEngine

) : CoroutineWorker(appContext, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {

        Log.d("TodayTaskWorker", "Worker Started")
        return try {

            val farms = repository.getMyFarmsOnce()
            Log.d("TodayTaskWorker", "Farm Count = ${farms.size}")

            var todayTaskCount = 0

            farms.forEach { farm ->

                val schedule = repository.getCropScheduleOnce(farm.cropId)
                val currentDay = daysSinceStart(farm.startDate)

                schedule?.phases
                    ?.flatMap { it.tasks }
                    ?.filter { it.day == currentDay }
                    ?.let { todayTaskCount += it.size }
            }

            if (todayTaskCount > 0) {
                notificationEngine.showTodayTaskNotification(
                    title = "🌾 RabiKrishi",
                    message = "Today you have $todayTaskCount farming task(s). Tap to view your schedule."
                )
            }

            Result.success()

        } catch (e: Exception) {
            Log.e("TodayTaskWorker", "Worker failed", e)
            Result.retry()
        }
    }

    // Normalizes both timestamps to midnight before diffing, so the
// day count doesn't shift based on what time of day startDate was saved.
    private fun daysSinceStart(startMillis: Long): Int {
        fun startOfDay(millis: Long) = java.util.Calendar.getInstance().apply {
            timeInMillis = millis
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

        val start = startOfDay(startMillis)
        val now = startOfDay(System.currentTimeMillis())

        return ((now - start) / (1000L * 60 * 60 * 24)).toInt()
    }

}