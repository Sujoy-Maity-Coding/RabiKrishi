package com.sujoy.smartfarm.Domain.repo

import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Domain.model.Expense.Expense
import kotlinx.coroutines.flow.Flow

interface FarmRepo {

    fun saveAIAnalysis(

        farmId: String,

        dailyUpdate: DailyFarmUpdate

    ): Flow<ResultState<String>>

}