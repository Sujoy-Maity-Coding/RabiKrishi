package com.sujoy.smartfarm.Data.Repo

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.R
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
import com.sujoy.smartfarm.Domain.repo.Repo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RepoImpl @Inject constructor(

    private val firebaseAuth: FirebaseAuth,

    private val firebaseFirestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context

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
                                context.getString(R.string.repo_msg_account_created)
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
                        context.getString(R.string.repo_msg_login_successful)
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

    override fun getFarmerProfile(): Flow<ResultState<FarmerData>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {
            trySend(ResultState.Error(context.getString(R.string.repo_err_user_not_logged_in)))
            close()
            return@callbackFlow
        }

        firebaseFirestore
            .collection("Farmers")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val farmer = document.toObject(FarmerData::class.java)
                if (farmer != null) {
                    trySend(ResultState.Success(farmer))
                } else {
                    trySend(ResultState.Error(context.getString(R.string.repo_err_profile_not_found)))
                }
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }

        awaitClose { close() }
    }

    override fun updateFarmerProfile(
        name: String,
        phoneNumber: String
    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {
            trySend(ResultState.Error(context.getString(R.string.repo_err_user_not_logged_in)))
            close()
            return@callbackFlow
        }

        firebaseFirestore
            .collection("Farmers")
            .document(uid)
            .update(
                mapOf(
                    "name" to name,
                    "phoneNumber" to phoneNumber
                )
            )
            .addOnSuccessListener {
                trySend(ResultState.Success(context.getString(R.string.repo_msg_profile_updated)))
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
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
                                context.getString(R.string.repo_err_no_suitable_crops)
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
        cropId: String, languageCode: String
    ): Flow<ResultState<CropMethod>> = callbackFlow {

        trySend(ResultState.Loading)

        val langCode = languageCode

        firebaseFirestore
            .collection("crop_methods")
            .document(cropId)
            .get()
            .addOnSuccessListener { document ->

                val cropMethod: CropMethod? = if (langCode == "en") {
                    document.toObject(CropMethod::class.java)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    val langMap = document.get(langCode) as? Map<String, Any>
                    if (langMap != null) {
                        mapToCropMethod(cropId, langMap)
                    } else {
                        // Fallback: translation missing for this crop, show English
                        document.toObject(CropMethod::class.java)
                    }
                }

                if (cropMethod != null) {
                    trySend(ResultState.Success(cropMethod))
                } else {
                    trySend(ResultState.Error(context.getString(R.string.repo_err_method_not_found)))
                }
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }

        awaitClose { close() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapToCropMethod(cropId: String, map: Map<String, Any>): CropMethod {
        fun list(key: String) = (map[key] as? List<String>) ?: emptyList()
        fun str(key: String) = (map[key] as? String) ?: ""

        return CropMethod(
            cropId = cropId,
            organicCost = str("organicCost"),
            organicYield = str("organicYield"),
            organicAdvantages = list("organicAdvantages"),
            organicDisadvantages = list("organicDisadvantages"),
            inorganicCost = str("inorganicCost"),
            inorganicYield = str("inorganicYield"),
            inorganicAdvantages = list("inorganicAdvantages"),
            inorganicDisadvantages = list("inorganicDisadvantages"),
            mixedCost = str("mixedCost"),
            mixedYield = str("mixedYield"),
            mixedAdvantages = list("mixedAdvantages"),
            mixedDisadvantages = list("mixedDisadvantages")
        )
    }

    override fun createFarm(

        farm: Farm

    ): Flow<ResultState<String>> =
        callbackFlow {

            trySend(ResultState.Loading)

            val uid = firebaseAuth.currentUser?.uid

            if (uid == null) {
                trySend(ResultState.Error(context.getString(R.string.repo_err_user_not_logged_in)))
                close()
                return@callbackFlow
            }

            // Use the farmId already set on the farm (generated upfront by the
            // ViewModel so the schedule can be saved under the same ID before
            // this farm doc even exists). Only generate a new one as a fallback.
            val farmId = farm.farmId.ifBlank {
                firebaseFirestore.collection("temp").document().id
            }

            val updatedFarm = farm.copy(farmId = farmId)

            firebaseFirestore
                .collection("Farmers")
                .document(uid)
                .collection("farms")
                .document(farmId)
                .set(updatedFarm)
                .addOnSuccessListener {
                    trySend(ResultState.Success(context.getString(R.string.repo_msg_farm_created)))
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
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
                        context.getString(R.string.repo_err_user_not_logged_in)
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
                        context.getString(R.string.repo_err_user_not_logged_in)
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
                                context.getString(R.string.repo_err_farm_not_found)
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
        cropId: String, languageCode: String
    ): Flow<ResultState<CropSchedule>> = callbackFlow {

        trySend(ResultState.Loading)

        val langCode = languageCode

        firebaseFirestore
            .collection("crop_schedules")
            .document(cropId)
            .get()
            .addOnSuccessListener { document ->

                val cropSchedule: CropSchedule? = if (langCode == "en") {
                    document.toObject(CropSchedule::class.java)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    val langMap = document.get(langCode) as? Map<String, Any>
                    if (langMap != null) {
                        mapToCropSchedule(cropId, langMap)
                    } else {
                        // Fallback: translation missing for this crop, show English
                        document.toObject(CropSchedule::class.java)
                    }
                }

                if (cropSchedule != null) {
                    Log.d("SCHEDULE", cropSchedule.toString())
                    trySend(ResultState.Success(cropSchedule))
                } else {
                    trySend(ResultState.Error(context.getString(R.string.repo_err_schedule_not_found)))
                }
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }

        awaitClose { close() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun mapToCropSchedule(cropId: String, map: Map<String, Any>): CropSchedule {
        fun str(key: String, m: Map<String, Any>) = (m[key] as? String) ?: ""
        fun int(key: String, m: Map<String, Any>) =
            (m[key] as? Long)?.toInt() ?: (m[key] as? Number)?.toInt() ?: 0

        val phasesRaw = (map["phases"] as? List<Map<String, Any>>) ?: emptyList()

        val phases = phasesRaw.map { phaseMap ->
            val tasksRaw = (phaseMap["tasks"] as? List<Map<String, Any>>) ?: emptyList()

            val tasks = tasksRaw.map { taskMap ->
                TaskItem(
                    taskId = str("taskId", taskMap),
                    day = int("day", taskMap),
                    title = str("title", taskMap),
                    description = str("description", taskMap)
                )
            }

            Phase(
                phaseId = str("phaseId", phaseMap),
                title = str("title", phaseMap),
                startDay = int("startDay", phaseMap),
                endDay = int("endDay", phaseMap),
                tasks = tasks
            )
        }

        return CropSchedule(
            cropId = cropId,
            phases = phases
        )
    }

    override fun updateTaskStatus(

        farmId: String,

        task: TaskItem,

        completed: Boolean

    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {

            trySend(ResultState.Error(context.getString(R.string.repo_err_user_not_logged_in)))

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

                                ResultState.Success(context.getString(R.string.repo_msg_updated))

                            )

                        }

                        .addOnFailureListener {

                            trySend(

                                ResultState.Error(

                                    it.message ?: context.getString(R.string.repo_err_failed)

                                )

                            )

                        }

                }

                .addOnFailureListener {

                    trySend(

                        ResultState.Error(

                            it.message ?: context.getString(R.string.repo_err_failed)

                        )

                    )

                }

        } else {

            completedTaskRef.delete()

                .addOnSuccessListener {

                    trySend(

                        ResultState.Success(context.getString(R.string.repo_msg_removed))

                    )

                }

                .addOnFailureListener {

                    trySend(

                        ResultState.Error(

                            it.message ?: context.getString(R.string.repo_err_generic)

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
                        context.getString(R.string.repo_err_user_not_logged_in)
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
                        context.getString(R.string.repo_err_user_not_logged_in)
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
                                                context.getString(R.string.repo_msg_saved_successfully)
                                            )

                                        )

                                    }

                                    .addOnFailureListener {

                                        trySend(

                                            ResultState.Error(

                                                it.message ?: context.getString(R.string.repo_err_failed_update_ai_status)

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
                                        context.getString(R.string.repo_msg_saved_successfully)
                                    )

                                )

                            }

                            .addOnFailureListener {

                                trySend(

                                    ResultState.Error(

                                        it.message ?: context.getString(R.string.repo_err_failed_update_ai_status)

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
                    ResultState.Error(context.getString(R.string.repo_err_user_not_logged_in))
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
                ResultState.Error(context.getString(R.string.repo_err_user_not_logged_in))
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
                            error.message ?: context.getString(R.string.repo_err_unknown)
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
                            ResultState.Error(context.getString(R.string.repo_err_no_data_found))
                        )
                    }

                } else {

                    trySend(
                        ResultState.Error(context.getString(R.string.repo_err_update_not_found))
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

            trySend(ResultState.Error(context.getString(R.string.repo_err_user_not_logged_in)))

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

                                error.message ?: context.getString(R.string.repo_err_unknown)

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

                                    context.getString(R.string.repo_err_failed_parse_ai_status)

                                )

                            )

                        }

                    } else {

                        trySend(

                            ResultState.Error(

                                context.getString(R.string.repo_err_no_ai_analysis_found)

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
                    ResultState.Error(context.getString(R.string.repo_err_user_not_logged_in))
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

                            context.getString(R.string.repo_msg_expense_added)

                        )

                    )

                }

                .addOnFailureListener {

                    trySend(

                        ResultState.Error(

                            it.message ?: context.getString(R.string.repo_err_failed)

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

                        context.getString(R.string.repo_err_user_not_logged_in)

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

    override suspend fun getMyFarmsOnce(): List<Farm> {

        val uid = firebaseAuth.currentUser?.uid
        Log.d("TodayTaskWorker", "getMyFarmsOnce: uid=$uid")

        if (uid == null) {
            Log.d("TodayTaskWorker", "getMyFarmsOnce: no user logged in, returning empty")
            return emptyList()
        }

        return try {
            val snapshot = firebaseFirestore
                .collection("Farmers")
                .document(uid)
                .collection("farms")
                .get()
                .await()

            Log.d("TodayTaskWorker", "getMyFarmsOnce: raw doc count = ${snapshot.documents.size}")

            snapshot.documents.mapNotNull {
                val farm = it.toObject(Farm::class.java)
                if (farm == null) {
                    Log.w("TodayTaskWorker", "getMyFarmsOnce: failed to map doc ${it.id} -> data=${it.data}")
                }
                farm
            }

        } catch (e: Exception) {
            Log.e("TodayTaskWorker", "getMyFarmsOnce: exception", e)
            emptyList()
        }
    }

    override suspend fun getCropScheduleOnce(
        cropId: String
    ): CropSchedule? {

        return try {

            firebaseFirestore
                .collection("crop_schedules")
                .document(cropId)
                .get()
                .await()
                .toObject(CropSchedule::class.java)

        } catch (e: Exception) {

            null

        }

    }
    override suspend fun getFarmSchedule(
        farmId: String
    ): CropSchedule? {

        val uid = firebaseAuth.currentUser?.uid ?: return null

        return try {

            firebaseFirestore
                .collection("Farmers")
                .document(uid)
                .collection("farms")
                .document(farmId)
                .collection("schedule")
                .document("current")
                .get()
                .await()
                .toObject(CropSchedule::class.java)

        } catch (e: Exception) {

            Log.e("FarmSchedule", "getFarmSchedule failed for $farmId", e)
            null

        }

    }

    override suspend fun saveFarmSchedule(
        farmId: String,
        schedule: CropSchedule
    ): Boolean {

        val uid = firebaseAuth.currentUser?.uid ?: return false

        return try {

            firebaseFirestore
                .collection("Farmers")
                .document(uid)
                .collection("farms")
                .document(farmId)
                .collection("schedule")
                .document("current")
                .set(schedule)
                .await()

            true

        } catch (e: Exception) {

            Log.e("FarmSchedule", "saveFarmSchedule failed for $farmId", e)
            false

        }

    }

    override suspend fun appendPhaseToFarmSchedule(
        farmId: String,
        newPhase: Phase
    ): Boolean {

        val uid = firebaseAuth.currentUser?.uid ?: return false

        val docRef = firebaseFirestore
            .collection("Farmers")
            .document(uid)
            .collection("farms")
            .document(farmId)
            .collection("schedule")
            .document("current")

        return try {

            firebaseFirestore.runTransaction { transaction ->

                val snapshot = transaction.get(docRef)

                val existing = snapshot.toObject(CropSchedule::class.java)

                if (existing == null) {

                    // No doc yet — shouldn't normally happen since farm creation
                    // always saves phase 1 first, but handle gracefully anyway.
                    transaction.set(
                        docRef,
                        CropSchedule(
                            cropId = "",
                            phases = listOf(newPhase)
                        )
                    )

                } else {

                    // Guard against double-tap: if this phase (by phaseId) is
                    // already present, don't add it again.
                    val alreadyHasPhase = existing.phases.any {
                        it.phaseId == newPhase.phaseId
                    }

                    if (!alreadyHasPhase) {

                        val updated = existing.copy(
                            phases = existing.phases + newPhase
                        )

                        transaction.set(docRef, updated)

                    }

                }

                null

            }.await()

            true

        } catch (e: Exception) {

            Log.e("FarmSchedule", "appendPhaseToFarmSchedule failed for $farmId", e)
            false

        }

    }

    override fun getFarmScheduleFlow(
        farmId: String
    ): Flow<ResultState<CropSchedule>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {

            trySend(ResultState.Error(context.getString(R.string.repo_err_user_not_logged_in)))
            close()
            return@callbackFlow

        }

        firebaseFirestore
            .collection("Farmers")
            .document(uid)
            .collection("farms")
            .document(farmId)
            .collection("schedule")
            .document("current")
            .get()
            .addOnSuccessListener { doc ->

                val schedule = doc.toObject(CropSchedule::class.java)

                if (schedule != null) {
                    trySend(ResultState.Success(schedule))
                } else {
                    trySend(ResultState.Error(context.getString(R.string.repo_err_schedule_not_found)))
                }

            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
            }

        awaitClose { close() }

    }

    // RepoImpl.kt
    override fun generateFarmId(): String {
        return firebaseFirestore.collection("temp").document().id
    }
}