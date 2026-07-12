package com.sujoy.smartfarm.Data.Repo

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.Domain.model.CompletedTask
import com.sujoy.smartfarm.Domain.model.Crop
import com.sujoy.smartfarm.Domain.model.CropMethod.CropMethod
import com.sujoy.smartfarm.Domain.model.CropSchedule
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Domain.model.Expense.Expense
import com.sujoy.smartfarm.Domain.model.Farm
import com.sujoy.smartfarm.Domain.model.FarmerData
import com.sujoy.smartfarm.Domain.model.TaskItem
import com.sujoy.smartfarm.Domain.repo.Repo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RepoImpl @Inject constructor(

    private val firebaseAuth: FirebaseAuth,

    private val firebaseFirestore: FirebaseFirestore,
    private val storage: FirebaseStorage

) : Repo {

    override fun signUp(

        farmerData: FarmerData,

        password: String

    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        firebaseAuth

            .createUserWithEmailAndPassword(

                farmerData.email,

                password

            )

            .addOnSuccessListener { authResult ->

                val uid = authResult.user?.uid ?: ""

                val updatedFarmer =

                    farmerData.copy(
                        userId = uid
                    )

                firebaseFirestore

                    .collection("Farmers")

                    .document(uid)

                    .set(updatedFarmer)

                    .addOnSuccessListener {

                        trySend(

                            ResultState.Success(
                                "Account Created Successfully"
                            )
                        )
                    }

                    .addOnFailureListener {

                        trySend(

                            ResultState.Error(
                                it.message.toString()
                            )
                        )
                    }
            }

            .addOnFailureListener {

                trySend(

                    ResultState.Error(
                        it.message.toString()
                    )
                )
            }

        awaitClose { close() }
    }

    override fun login(

        email: String,

        password: String

    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        firebaseAuth

            .signInWithEmailAndPassword(

                email,

                password

            )

            .addOnSuccessListener {

                trySend(

                    ResultState.Success(
                        "Login Successful"
                    )
                )
            }

            .addOnFailureListener {

                trySend(

                    ResultState.Error(
                        it.message.toString()
                    )
                )
            }

        awaitClose { close() }
    }

    override fun isUserLoggedIn(): Boolean {

        return firebaseAuth.currentUser != null
    }

    override fun logout() {

        firebaseAuth.signOut()
    }
    override fun getRecommendedCrops(

        district: String,

        month: Int,

        season: String,

        soilType: String

    ): Flow<ResultState<List<Crop>>> =
        callbackFlow {

            trySend(ResultState.Loading)

            firebaseFirestore

                .collection("crop_recommendations")

                .get()

                .addOnSuccessListener { result ->
                    Log.d(
                        "FIRESTORE",
                        "Docs = ${result.documents.size}"
                    )

                    val crops = mutableListOf<Crop>()

                    result.documents.forEach { doc ->
                        Log.d(
                            "FIRESTORE",
                            doc.data.toString()
                        )

                        val crop =
                            doc.toObject(
                                Crop::class.java
                            )

                        crop?.let {

                            var score = 0

                            if(
                                it.districts.any {
                                        districtName ->

                                    districtName.equals(
                                        district,
                                        ignoreCase = true
                                    )
                                }
                            ) {
                                score += 20
                            }

                            if(month in it.months) {
                                score += 30
                            }

                            if(
                                it.season.equals(
                                    season,
                                    ignoreCase = true
                                )
                            ) {
                                score += 30
                            }

                            if(
                                it.soilTypes.any {
                                        soil ->

                                    soil.equals(
                                        soilType,
                                        ignoreCase = true
                                    )
                                }
                            ) {
                                score += 20
                            }

                            if(score >= 40) {

                                crops.add(

                                    it.copy(
                                        recommendationScore = score
                                    )
                                )
                            }
                        }
                    }

                    val sortedList =

                        crops.sortedByDescending {

                            it.recommendationScore
                        }
                    if(sortedList.isEmpty()) {

                        trySend(
                            ResultState.Error(
                                "No suitable crops found for selected conditions."
                            )
                        )

                        return@addOnSuccessListener
                    }

                    trySend(
                        ResultState.Success(
                            sortedList.take(3)
                        )
                    )
                }

                .addOnFailureListener {

                    trySend(
                        ResultState.Error(
                            it.message.toString()
                        )
                    )
                }

            awaitClose { close() }
        }

    override fun getCropMethod(

        cropId: String

    ): Flow<ResultState<CropMethod>> = callbackFlow {

        trySend(ResultState.Loading)

        firebaseFirestore

            .collection("crop_methods")

            .document(cropId)

            .get()

            .addOnSuccessListener { document ->

                val cropMethod =

                    document.toObject(
                        CropMethod::class.java
                    )

                if(cropMethod != null){

                    trySend(

                        ResultState.Success(
                            cropMethod
                        )
                    )

                } else {

                    trySend(

                        ResultState.Error(
                            "Method not found"
                        )
                    )
                }
            }

            .addOnFailureListener {

                trySend(

                    ResultState.Error(
                        it.message.toString()
                    )
                )
            }

        awaitClose { close() }
    }

    override fun createFarm(

        farm: Farm

    ): Flow<ResultState<String>> =
        callbackFlow {

            trySend(ResultState.Loading)

            val uid =
                firebaseAuth.currentUser?.uid

            if(uid == null){

                trySend(
                    ResultState.Error(
                        "User not logged in"
                    )
                )

                close()

                return@callbackFlow
            }

            val farmId =

                firebaseFirestore
                    .collection("temp")
                    .document()
                    .id

            val updatedFarm =

                farm.copy(
                    farmId = farmId
                )

            firebaseFirestore

                .collection("Farmers")

                .document(uid)

                .collection("farms")

                .document(farmId)

                .set(updatedFarm)

                .addOnSuccessListener {

                    trySend(

                        ResultState.Success(
                            "Farm Created"
                        )
                    )
                }

                .addOnFailureListener {

                    trySend(

                        ResultState.Error(
                            it.message.toString()
                        )
                    )
                }

            awaitClose { close() }
        }
    override fun getMyFarms():
            Flow<ResultState<List<Farm>>> =
        callbackFlow {

            trySend(ResultState.Loading)

            val uid =
                firebaseAuth.currentUser?.uid

            if (uid == null) {

                trySend(
                    ResultState.Error(
                        "User not logged in"
                    )
                )

                close()

                return@callbackFlow
            }

            val listener =

                firebaseFirestore

                    .collection("Farmers")

                    .document(uid)

                    .collection("farms")

                    .addSnapshotListener { value, error ->

                        if (error != null) {

                            trySend(
                                ResultState.Error(
                                    error.message.toString()
                                )
                            )

                            return@addSnapshotListener
                        }

                        val farms =
                            value?.documents
                                ?.mapNotNull {
                                    Log.d(
                                        "FARM_DOC",
                                        it.data.toString()
                                    )

                                    it.toObject(
                                        Farm::class.java
                                    )
                                }
                                ?: emptyList()

                        trySend(
                            ResultState.Success(
                                farms
                            )
                        )
                    }

            awaitClose {

                listener.remove()
            }
        }
    override fun getFarmById(

        farmId: String

    ): Flow<ResultState<Farm>> =
        callbackFlow {

            trySend(ResultState.Loading)

            val uid =
                firebaseAuth.currentUser?.uid

            if(uid == null){

                trySend(
                    ResultState.Error(
                        "User not logged in"
                    )
                )

                close()

                return@callbackFlow
            }

            firebaseFirestore

                .collection("Farmers")

                .document(uid)

                .collection("farms")

                .document(farmId)

                .get()

                .addOnSuccessListener { document ->

                    val farm =

                        document.toObject(
                            Farm::class.java
                        )

                    if(farm != null){

                        trySend(
                            ResultState.Success(
                                farm
                            )
                        )

                    } else {

                        trySend(
                            ResultState.Error(
                                "Farm not found"
                            )
                        )
                    }
                }

                .addOnFailureListener {

                    trySend(
                        ResultState.Error(
                            it.message.toString()
                        )
                    )
                }

            awaitClose { close() }
        }
    override fun getCropSchedule(

        cropId: String

    ): Flow<ResultState<CropSchedule>> =
        callbackFlow {

            trySend(ResultState.Loading)

            firebaseFirestore

                .collection("crop_schedules")

                .document(cropId)

                .get()

                .addOnSuccessListener { document ->

                    val schedule =

                        document.toObject(
                            CropSchedule::class.java
                        )

                    if(schedule != null){
                        Log.d(
                            "SCHEDULE",
                            schedule.toString()
                        )

                        trySend(

                            ResultState.Success(
                                schedule
                            )
                        )

                    } else {

                        trySend(

                            ResultState.Error(
                                "Schedule not found"
                            )
                        )
                    }
                }

                .addOnFailureListener {

                    trySend(

                        ResultState.Error(
                            it.message.toString()
                        )
                    )
                }

            awaitClose { close() }
        }

    override fun updateTaskStatus(

        farmId: String,

        task: TaskItem,

        completed: Boolean

    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {

            trySend(ResultState.Error("User not logged in"))

            close()

            return@callbackFlow

        }

        val farmRef = firebaseFirestore
            .collection("Farmers")
            .document(uid)
            .collection("farms")
            .document(farmId)

        val completedTaskRef = farmRef
            .collection("completed_tasks")
            .document(task.taskId)

        if (completed) {

            farmRef.get()

                .addOnSuccessListener { document ->

                    val farm = document.toObject(Farm::class.java)
                        ?: return@addOnSuccessListener

                    completedTaskRef

                        .set(

                            CompletedTask(

                                taskId = task.taskId,

                                taskDay = task.day,

                                completed = true,

                                completedAt = System.currentTimeMillis()

                            )

                        )

                        .addOnSuccessListener {

                            trySend(

                                ResultState.Success("Updated")

                            )

                        }

                        .addOnFailureListener {

                            trySend(

                                ResultState.Error(

                                    it.message ?: "Failed"

                                )

                            )

                        }

                }

                .addOnFailureListener {

                    trySend(

                        ResultState.Error(

                            it.message ?: "Failed"

                        )

                    )

                }

        } else {

            completedTaskRef.delete()

                .addOnSuccessListener {

                    trySend(

                        ResultState.Success("Removed")

                    )

                }

                .addOnFailureListener {

                    trySend(

                        ResultState.Error(

                            it.message ?: "Error"

                        )

                    )

                }

        }

        awaitClose {

            close()

        }

    }

    override fun getCompletedTasks(

        farmId: String

    ): Flow<ResultState<List<CompletedTask>>> =
        callbackFlow {

            trySend(ResultState.Loading)

            val uid =
                firebaseAuth.currentUser?.uid

            if(uid == null){

                trySend(
                    ResultState.Error(
                        "User not logged in"
                    )
                )

                close()

                return@callbackFlow
            }

            val listener =

                firebaseFirestore

                    .collection("Farmers")

                    .document(uid)

                    .collection("farms")

                    .document(farmId)

                    .collection("completed_tasks")

                    .addSnapshotListener {

                            value,
                            error

                        ->

                        if(error != null){

                            trySend(
                                ResultState.Error(
                                    error.message.toString()
                                )
                            )

                            return@addSnapshotListener
                        }

                        val completedTasks =

                            value?.documents
                                ?.mapNotNull {

                                    it.toObject(

                                        CompletedTask::class.java

                                    )

                                }
                                ?.filter {

                                    it.completed

                                }
                                ?: emptyList()

                        trySend(

                            ResultState.Success(
                                completedTasks
                            )
                        )
                    }

            awaitClose {

                listener.remove()
            }
        }

    override fun saveDailyUpdate(

        farmId: String,

        update: DailyFarmUpdate,

        imageUri: Uri?

    ): Flow<ResultState<String>> =
        callbackFlow {

            trySend(ResultState.Loading)

            val uid =
                firebaseAuth.currentUser?.uid

            if(uid == null){

                trySend(
                    ResultState.Error(
                        "User not logged in"
                    )
                )

                close()

                return@callbackFlow
            }

            val document =

                firebaseFirestore

                    .collection("Farmers")

                    .document(uid)

                    .collection("farms")

                    .document(farmId)

                    .collection("daily_updates")

                    .document()

            val updateId = document.id
            if(imageUri != null){

                val storageRef =

                    storage.reference

                        .child(

                            "daily_updates/$uid/$farmId/$updateId.jpg"
                        )

                storageRef

                    .putFile(imageUri)

                    .continueWithTask {

                        storageRef.downloadUrl

                    }

                    .addOnSuccessListener {

                        val imageUrl =
                            it.toString()

                        val finalUpdate =

                            update.copy(

                                updateId = updateId,

                                imageUrl = imageUrl
                            )

                        document

                            .set(finalUpdate)

                            .addOnSuccessListener {

                                firebaseFirestore

                                    .collection("Farmers")

                                    .document(uid)

                                    .collection("farms")

                                    .document(farmId)

                                    .collection("latest_ai_status")

                                    .document("current")

                                    .set(finalUpdate)

                                    .addOnSuccessListener {

                                        trySend(

                                            ResultState.Success(
                                                "Saved Successfully"
                                            )

                                        )

                                    }

                                    .addOnFailureListener {

                                        trySend(

                                            ResultState.Error(

                                                it.message ?: "Failed to update latest AI status"

                                            )

                                        )

                                    }

                            }
                    }

                    .addOnFailureListener {

                        trySend(

                            ResultState.Error(
                                it.message.toString()
                            )
                        )
                    }
            }else{

                val finalUpdate = update.copy(

                    updateId = updateId

                )

                document

                    .set(finalUpdate)

                    .addOnSuccessListener {

                        firebaseFirestore

                            .collection("Farmers")

                            .document(uid)

                            .collection("farms")

                            .document(farmId)

                            .collection("latest_ai_status")

                            .document("current")

                            .set(finalUpdate)

                            .addOnSuccessListener {

                                trySend(

                                    ResultState.Success(
                                        "Saved Successfully"
                                    )

                                )

                            }

                            .addOnFailureListener {

                                trySend(

                                    ResultState.Error(

                                        it.message ?: "Failed to update latest AI status"

                                    )

                                )

                            }

                    }

                    .addOnFailureListener {

                        trySend(

                            ResultState.Error(
                                it.message.toString()
                            )
                        )
                    }
            }

            awaitClose{

                close()
            }
        }
    override fun getDailyUpdates(

        farmId: String

    ): Flow<ResultState<List<DailyFarmUpdate>>> =
        callbackFlow {

            trySend(ResultState.Loading)

            val uid = firebaseAuth.currentUser?.uid

            if (uid == null) {

                trySend(
                    ResultState.Error("User not logged in")
                )

                close()

                return@callbackFlow
            }

            val listener =

                firebaseFirestore

                    .collection("Farmers")

                    .document(uid)

                    .collection("farms")

                    .document(farmId)

                    .collection("daily_updates")

                    .orderBy("day")

                    .addSnapshotListener { value, error ->

                        if (error != null) {

                            trySend(
                                ResultState.Error(
                                    error.message.toString()
                                )
                            )

                            return@addSnapshotListener
                        }

                        val updates =

                            value?.toObjects(
                                DailyFarmUpdate::class.java
                            ) ?: emptyList()

                        trySend(
                            ResultState.Success(updates)
                        )
                    }

            awaitClose {

                listener.remove()
            }
        }

    override fun getDailyUpdate(

        farmId: String,

        updateId: String

    ): Flow<ResultState<DailyFarmUpdate>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {

            trySend(
                ResultState.Error("User not logged in")
            )

            close()

            return@callbackFlow
        }

        val listener = firebaseFirestore

            .collection("Farmers")

            .document(uid)

            .collection("farms")

            .document(farmId)

            .collection("daily_updates")

            .document(updateId)

            .addSnapshotListener { snapshot, error ->

                if (error != null) {

                    trySend(
                        ResultState.Error(
                            error.message ?: "Unknown error"
                        )
                    )

                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {

                    val update = snapshot.toObject(
                        DailyFarmUpdate::class.java
                    )

                    if (update != null) {

                        trySend(
                            ResultState.Success(update)
                        )

                    } else {

                        trySend(
                            ResultState.Error("No data found")
                        )
                    }

                } else {

                    trySend(
                        ResultState.Error("Update not found")
                    )
                }
            }

        awaitClose {

            listener.remove()
        }
    }

    override fun getLatestAIStatus(

        farmId: String

    ): Flow<ResultState<DailyFarmUpdate>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {

            trySend(ResultState.Error("User not logged in"))

            close()

            return@callbackFlow

        }

        val listener =

            firebaseFirestore

                .collection("Farmers")

                .document(uid)

                .collection("farms")

                .document(farmId)

                .collection("latest_ai_status")

                .document("current")

                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        trySend(

                            ResultState.Error(

                                error.message ?: "Unknown Error"

                            )

                        )

                        return@addSnapshotListener

                    }

                    if (

                        snapshot != null &&

                        snapshot.exists()

                    ) {

                        val update =

                            snapshot.toObject(

                                DailyFarmUpdate::class.java

                            )

                        if (update != null) {

                            trySend(

                                ResultState.Success(update)

                            )

                        } else {

                            trySend(

                                ResultState.Error(

                                    "Failed to parse AI status"

                                )

                            )

                        }

                    } else {

                        trySend(

                            ResultState.Error(

                                "No AI analysis found"

                            )

                        )

                    }

                }

        awaitClose {

            listener.remove()

        }

    }

    override fun addExpense(

        farmId: String,

        expense: Expense

    ): Flow<ResultState<String>> =
        callbackFlow {

            trySend(ResultState.Loading)

            val uid = firebaseAuth.currentUser?.uid

            if (uid == null) {

                trySend(
                    ResultState.Error("User not logged in")
                )

                close()

                return@callbackFlow

            }

            val expenseId =

                firebaseFirestore

                    .collection("temp")

                    .document()

                    .id

            val finalExpense =

                expense.copy(

                    expenseId = expenseId

                )

            firebaseFirestore

                .collection("Farmers")

                .document(uid)

                .collection("farms")

                .document(farmId)

                .collection("expenses")

                .document(expenseId)

                .set(finalExpense)

                .addOnSuccessListener {

                    trySend(

                        ResultState.Success(

                            "Expense Added"

                        )

                    )

                }

                .addOnFailureListener {

                    trySend(

                        ResultState.Error(

                            it.message ?: "Failed"

                        )

                    )

                }

            awaitClose {

                close()

            }

        }

    override fun getExpenses(

        farmId: String

    ): Flow<ResultState<List<Expense>>> =
        callbackFlow {

            trySend(ResultState.Loading)

            val uid = firebaseAuth.currentUser?.uid

            if (uid == null) {

                trySend(

                    ResultState.Error(

                        "User not logged in"

                    )

                )

                close()

                return@callbackFlow

            }

            val listener =

                firebaseFirestore

                    .collection("Farmers")

                    .document(uid)

                    .collection("farms")

                    .document(farmId)

                    .collection("expenses")

                    .orderBy(

                        "date",

                        Query.Direction.DESCENDING

                    )

                    .addSnapshotListener {

                            value,
                            error ->

                        if (error != null) {

                            trySend(

                                ResultState.Error(

                                    error.message ?: ""

                                )

                            )

                            return@addSnapshotListener

                        }

                        val expenses =

                            value?.toObjects(

                                Expense::class.java

                            ) ?: emptyList()

                        trySend(

                            ResultState.Success(

                                expenses

                            )

                        )

                    }

            awaitClose {

                listener.remove()

            }

        }
}