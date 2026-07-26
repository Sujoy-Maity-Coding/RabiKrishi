package com.sujoy.smartfarm.Domain.repo

import android.net.Uri
import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.Domain.model.CompletedTask
import com.sujoy.smartfarm.Domain.model.Crop
import com.sujoy.smartfarm.Domain.model.CropMethod.CropMethod
import com.sujoy.smartfarm.Domain.model.CropSchedule
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Domain.model.Expense.Expense
import com.sujoy.smartfarm.Domain.model.Farm
import com.sujoy.smartfarm.Domain.model.FarmerData
import com.sujoy.smartfarm.Domain.model.Phase
import com.sujoy.smartfarm.Domain.model.TaskItem
import kotlinx.coroutines.flow.Flow

interface Repo {

    fun signUp(

        farmerData: FarmerData,

        password: String

    ): Flow<ResultState<String>>

    fun login(

        email: String,

        password: String

    ): Flow<ResultState<String>>

    fun getFarmerProfile(): Flow<ResultState<FarmerData>>
    fun updateFarmerProfile(name: String, phoneNumber: String): Flow<ResultState<String>>

    fun isUserLoggedIn(): Boolean

    fun logout()

    fun getRecommendedCrops(

        district: String,

        month: Int,

        season: String,

        soilType: String

    ): Flow<ResultState<List<Crop>>>

    fun getCropMethod(
        cropId: String, languageCode: String
    ): Flow<ResultState<CropMethod>>

    fun createFarm(

        farm: Farm

    ): Flow<ResultState<String>>

    fun getMyFarms():

            Flow<ResultState<List<Farm>>>

    fun getFarmById(

        farmId: String

    ): Flow<ResultState<Farm>>

    fun getCropSchedule(
        cropId: String, languageCode: String
    ): Flow<ResultState<CropSchedule>>

    fun updateTaskStatus(

        farmId: String,

        task: TaskItem,

        completed: Boolean

    ): Flow<ResultState<String>>

    fun getCompletedTasks(

        farmId: String

    ): Flow<ResultState<List<CompletedTask>>>

    fun saveDailyUpdate(

        farmId: String,

        update: DailyFarmUpdate,

        imageUri: Uri?

    ): Flow<ResultState<String>>
    fun getDailyUpdates(

        farmId: String

    ): Flow<ResultState<List<DailyFarmUpdate>>>

    fun getDailyUpdate(

        farmId: String,

        updateId: String

    ): Flow<ResultState<DailyFarmUpdate>>

    fun getLatestAIStatus(

        farmId: String

    ): Flow<ResultState<DailyFarmUpdate>>

    fun addExpense(

        farmId: String,

        expense: Expense

    ): Flow<ResultState<String>>

    fun getExpenses(

        farmId: String

    ): Flow<ResultState<List<Expense>>>

    suspend fun getMyFarmsOnce(): List<Farm>

    suspend fun getCropScheduleOnce(
        cropId: String
    ): CropSchedule?

    suspend fun getFarmSchedule(
        farmId: String
    ): CropSchedule?

    suspend fun saveFarmSchedule(
        farmId: String,
        schedule: CropSchedule
    ): Boolean

    suspend fun appendPhaseToFarmSchedule(
        farmId: String,
        newPhase: Phase
    ): Boolean

    fun getFarmScheduleFlow(
        farmId: String
    ): Flow<ResultState<CropSchedule>>

    // Repo.kt
    fun generateFarmId(): String
}