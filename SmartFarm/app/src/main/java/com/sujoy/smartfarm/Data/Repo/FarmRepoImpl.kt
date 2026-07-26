package com.sujoy.smartfarm.Data.Repo

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Domain.model.Expense.Expense
import com.sujoy.smartfarm.Domain.repo.FarmRepo
import com.sujoy.smartfarm.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FarmRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context

) : FarmRepo {

    override fun saveAIAnalysis(

        farmId: String,

        dailyUpdate: DailyFarmUpdate

    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        firestore

            .collection("Farms")

            .document(farmId)

            .collection("dailyUpdates")

            .document("day${dailyUpdate.day}")

            .set(dailyUpdate)

            .addOnSuccessListener {

                firestore

                    .collection("Farms")

                    .document(farmId)

                    .collection("latestAIStatus")

                    .document("current")

                    .set(dailyUpdate.aiResult)

                    .addOnSuccessListener {

                        trySend(

                            ResultState.Success(

                                context.getString(R.string.farm_repo_msg_ai_saved)

                            )

                        )

                    }

                    .addOnFailureListener {

                        trySend(

                            ResultState.Error(

                                it.message ?: context.getString(R.string.repo_err_unknown)

                            )

                        )

                    }

            }

            .addOnFailureListener {

                trySend(

                    ResultState.Error(

                        it.message ?: context.getString(R.string.repo_err_unknown)

                    )

                )

            }

        awaitClose { close() }

    }

}