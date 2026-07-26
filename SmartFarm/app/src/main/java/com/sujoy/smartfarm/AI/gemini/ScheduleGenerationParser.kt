package com.sujoy.smartfarm.AI.gemini

import com.google.gson.Gson
import com.sujoy.smartfarm.Domain.model.Phase
import javax.inject.Inject

class ScheduleGenerationParser @Inject constructor() {

    private val gson = Gson()

    fun parse(response: String): Phase? {

        return try {

            val cleaned = response
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val phase = gson.fromJson(cleaned, Phase::class.java)

            if (isValid(phase)) phase else null

        } catch (e: Exception) {

            null

        }

    }

    private fun isValid(phase: Phase?): Boolean {

        if (phase == null) return false
        if (phase.tasks.isEmpty()) return false

        for (task in phase.tasks) {
            if (task.title.isBlank() || task.description.isBlank()) return false
        }

        return true

    }

}