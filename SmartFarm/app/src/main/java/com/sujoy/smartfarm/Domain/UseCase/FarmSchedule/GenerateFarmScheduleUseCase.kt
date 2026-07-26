package com.sujoy.smartfarm.Domain.UseCase

import com.google.gson.Gson
import com.sujoy.smartfarm.AI.model.ScheduleGenerationRequest
import com.sujoy.smartfarm.Domain.model.CropSchedule
import com.sujoy.smartfarm.Domain.model.Phase
import com.sujoy.smartfarm.Domain.repo.GeminiRepository
import com.sujoy.smartfarm.Domain.repo.Repo
import javax.inject.Inject

class GenerateFarmScheduleUseCase @Inject constructor(

    private val repo: Repo,

    private val geminiRepository: GeminiRepository

) {

    /**
     * Called once, right after a farm is created. Generates and saves
     * ONLY the first phase from the base schedule.
     */
    suspend fun generateInitialPhase(

        farmId: String,
        cropId: String,
        cropName: String,
        farmingMethod: String,
        district: String,
        season: String,
        farmSize: Double,
        languageCode: String

    ): Result<Unit> {

        val baseSchedule = repo.getCropScheduleOnce(cropId)
            ?: return Result.failure(Exception("No base schedule found for $cropId"))

        val basePhases = baseSchedule.phases

        if (basePhases.isEmpty()) {
            return Result.failure(Exception("Base schedule has no phases"))
        }

        val basePhase = basePhases[0]

        val phaseToSave = generatePhase(
            basePhase, cropId, cropName, farmingMethod,
            district, season, farmSize, languageCode
        ).getOrElse { basePhase }   // AI failed → fall back to verified base phase, never block farm creation

        val saved = repo.saveFarmSchedule(

            farmId,

            CropSchedule(cropId = cropId, phases = listOf(phaseToSave))

        )

        return if (saved) Result.success(Unit)
        else Result.failure(Exception("Could not save initial schedule"))

    }

    /**
     * Called when the farmer taps "Continue". Figures out which base-schedule
     * phase comes next (by counting how many phases already exist), generates
     * it, and appends it to the farm's schedule doc.
     */
    suspend fun generateNextPhase(

        farmId: String,
        cropId: String,
        cropName: String,
        farmingMethod: String,
        district: String,
        season: String,
        farmSize: Double,
        languageCode: String

    ): Result<Unit> {

        val existing = repo.getFarmSchedule(farmId)
            ?: return Result.failure(Exception("No existing schedule found for this farm"))

        val baseSchedule = repo.getCropScheduleOnce(cropId)
            ?: return Result.failure(Exception("No base schedule found for $cropId"))

        val basePhases = baseSchedule.phases

        val nextIndex = existing.phases.size

        if (nextIndex >= basePhases.size) {

            return Result.failure(Exception("Schedule is already complete — no more phases to generate"))

        }

        val basePhase = basePhases[nextIndex]

        val phaseToAppend = generatePhase(
            basePhase, cropId, cropName, farmingMethod,
            district, season, farmSize, languageCode
        ).getOrElse { basePhase }   // AI failed → append verified base phase instead of blocking the farmer

        val appended = repo.appendPhaseToFarmSchedule(farmId, phaseToAppend)

        return if (appended) Result.success(Unit)
        else Result.failure(Exception("Could not save the next phase"))

    }

    /**
     * True once every phase from the base schedule has been generated —
     * used to hide/disable the Continue button.
     */
    suspend fun isScheduleComplete(farmId: String, cropId: String): Boolean {

        val existing = repo.getFarmSchedule(farmId) ?: return false
        val baseSchedule = repo.getCropScheduleOnce(cropId) ?: return false

        return existing.phases.size >= baseSchedule.phases.size

    }

    private suspend fun generatePhase(

        basePhase: Phase,
        cropId: String,
        cropName: String,
        farmingMethod: String,
        district: String,
        season: String,
        farmSize: Double,
        languageCode: String

    ): Result<Phase> {

        val request = ScheduleGenerationRequest(

            cropId = cropId,
            cropName = cropName,
            farmingMethod = farmingMethod,
            district = district,
            season = season,
            farmSize = farmSize,
            languageCode = languageCode,
            basePhaseJson = Gson().toJson(basePhase)

        )

        return geminiRepository.generateSchedule(request)

    }

}