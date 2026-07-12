package com.sujoy.smartfarm.Data.Repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Domain.model.Expense.Expense
import com.sujoy.smartfarm.Domain.repo.FarmRepo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FarmRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore

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

                                "AI Analysis Saved Successfully"

                            )

                        )

                    }

                    .addOnFailureListener {

                        trySend(

                            ResultState.Error(

                                it.message ?: "Unknown Error"

                            )

                        )

                    }

            }

            .addOnFailureListener {

                trySend(

                    ResultState.Error(

                        it.message ?: "Unknown Error"

                    )

                )

            }

        awaitClose { close() }

    }

}