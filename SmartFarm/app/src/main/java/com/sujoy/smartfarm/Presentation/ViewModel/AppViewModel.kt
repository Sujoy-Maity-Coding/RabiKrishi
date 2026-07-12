package com.sujoy.smartfarm.Presentation.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.smartfarm.Common.ResultState
import com.sujoy.smartfarm.Domain.UseCase.AnalyzeCrop.GetLatestAIStatusUseCase
import com.sujoy.smartfarm.Domain.UseCase.CreateFarmUseCase
import com.sujoy.smartfarm.Domain.UseCase.GetCompletedTasksUseCase
import com.sujoy.smartfarm.Domain.UseCase.CropMethod.GetCropMethodUseCase
import com.sujoy.smartfarm.Domain.UseCase.Expense.AddExpenseUseCase
import com.sujoy.smartfarm.Domain.UseCase.Expense.GetExpensesUseCase
import com.sujoy.smartfarm.Domain.UseCase.GetCropScheduleUseCase
import com.sujoy.smartfarm.Domain.UseCase.GetDailyUpdateUseCase
import com.sujoy.smartfarm.Domain.UseCase.GetDailyUpdatesUseCase
import com.sujoy.smartfarm.Domain.UseCase.GetFarmByIdUseCase
import com.sujoy.smartfarm.Domain.UseCase.GetMyFarmsUseCase
import com.sujoy.smartfarm.Domain.UseCase.GetRecommendedCropsUseCase
import com.sujoy.smartfarm.Domain.UseCase.IsUserLoggedInUseCase
import com.sujoy.smartfarm.Domain.UseCase.LoginUseCase
import com.sujoy.smartfarm.Domain.UseCase.LogoutUseCase
import com.sujoy.smartfarm.Domain.UseCase.SaveDailyUpdateUseCase
import com.sujoy.smartfarm.Domain.UseCase.SignUpUseCase
import com.sujoy.smartfarm.Domain.UseCase.UpdateTaskStatusUseCase
import com.sujoy.smartfarm.Domain.model.DailyFarmUpdate
import com.sujoy.smartfarm.Domain.model.Expense.Expense
import com.sujoy.smartfarm.Domain.model.Farm
import com.sujoy.smartfarm.Domain.model.FarmerData
import com.sujoy.smartfarm.Domain.model.TaskItem
import com.sujoy.smartfarm.Presentation.State.AnalyzeCrop.LatestAIState
import com.sujoy.smartfarm.Presentation.State.AuthState
import com.sujoy.smartfarm.Presentation.State.CompletedTasksState
import com.sujoy.smartfarm.Presentation.State.RecomMethodCreate.CreateFarmState
import com.sujoy.smartfarm.Presentation.State.CropHealthHistoryState
import com.sujoy.smartfarm.Presentation.State.CropHistory.CropHistoryState
import com.sujoy.smartfarm.Presentation.State.RecomMethodCreate.CropMethodState
import com.sujoy.smartfarm.Presentation.State.CropScheduleState
import com.sujoy.smartfarm.Presentation.State.CropUpdateDetailsState
import com.sujoy.smartfarm.Presentation.State.Expense.AddExpenseState
import com.sujoy.smartfarm.Presentation.State.Expense.ExpenseListState
import com.sujoy.smartfarm.Presentation.State.FarmDetailsState
import com.sujoy.smartfarm.Presentation.State.RecomMethodCreate.MyFarmsState
import com.sujoy.smartfarm.Presentation.State.RecomMethodCreate.RecommendationState
import com.sujoy.smartfarm.Presentation.State.SaveDailyUpdateState
import com.sujoy.smartfarm.Presentation.State.TaskUpdateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(

    private val signUpUseCase: SignUpUseCase,

    private val loginUseCase: LoginUseCase,

    private val isUserLoggedInUseCase:
    IsUserLoggedInUseCase,

    private val logoutUseCase:
    LogoutUseCase,

    private val getRecommendedCropsUseCase:
    GetRecommendedCropsUseCase,

    private val getCropMethodUseCase:
    GetCropMethodUseCase,

    private val createFarmUseCase:
    CreateFarmUseCase,

    private val getMyFarmsUseCase:
    GetMyFarmsUseCase,

    private val getCropScheduleUseCase:
    GetCropScheduleUseCase,

    private val getFarmByIdUseCase:
    GetFarmByIdUseCase,

    private val updateTaskStatusUseCase:
    UpdateTaskStatusUseCase,

    private val getCompletedTasksUseCase:
    GetCompletedTasksUseCase,

    private val saveDailyUpdateUseCase:
    SaveDailyUpdateUseCase,
    private val getDailyUpdatesUseCase:
    GetDailyUpdatesUseCase,
    private val getDailyUpdateUseCase: GetDailyUpdateUseCase,
    private val getLatestAIStatusUseCase: GetLatestAIStatusUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,

    private val getExpensesUseCase: GetExpensesUseCase,
    ) : ViewModel() {
    private val _authState =

        MutableStateFlow(
            AuthState()
        )

    val authState =
        _authState.asStateFlow()

    private val _recommendationState =

        MutableStateFlow(
            RecommendationState()
        )

    val recommendationState =
        _recommendationState.asStateFlow()

    private val _cropMethodState =

        MutableStateFlow(
            CropMethodState()
        )

    val cropMethodState =
        _cropMethodState.asStateFlow()

    private val _createFarmState =

        MutableStateFlow(
            CreateFarmState()
        )

    val createFarmState =
        _createFarmState.asStateFlow()

    private val _myFarmsState =

        MutableStateFlow(
            MyFarmsState()
        )

    val myFarmsState =
        _myFarmsState.asStateFlow()

    private val _cropScheduleState =

        MutableStateFlow(
            CropScheduleState()
        )

    val cropScheduleState =
        _cropScheduleState.asStateFlow()

    private val _farmDetailsState =
        MutableStateFlow(
            FarmDetailsState()
        )

    val farmDetailsState =
        _farmDetailsState.asStateFlow()

    private val _taskUpdateState =

        MutableStateFlow(
            TaskUpdateState()
        )

    val taskUpdateState =
        _taskUpdateState.asStateFlow()

    private val _completedTasksState =

        MutableStateFlow(
            CompletedTasksState()
        )

    val completedTasksState =
        _completedTasksState.asStateFlow()

    private val _saveDailyUpdateState =

        MutableStateFlow(
            SaveDailyUpdateState()
        )

    val saveDailyUpdateState =
        _saveDailyUpdateState.asStateFlow()
    private val _cropHealthHistoryState =

        MutableStateFlow(
            CropHealthHistoryState()
        )

    val cropHealthHistoryState =
        _cropHealthHistoryState.asStateFlow()

    private val _cropUpdateDetailsState =
        MutableStateFlow(
            CropUpdateDetailsState()
        )

    private val _latestAIState =

        MutableStateFlow(
            LatestAIState()
        )

    val latestAIState =

        _latestAIState.asStateFlow()

    private val _cropHistoryState =
        MutableStateFlow(CropHistoryState())

    val cropHistoryState =
        _cropHistoryState.asStateFlow()

    private val _addExpenseState =
        MutableStateFlow(AddExpenseState())

    val addExpenseState =
        _addExpenseState.asStateFlow()

    private val _expenseListState =
        MutableStateFlow(ExpenseListState())

    val expenseListState =
        _expenseListState.asStateFlow()

    fun signUp(

        farmerData: FarmerData,

        password: String

    ) {

        viewModelScope.launch {

            signUpUseCase

                .signUp(

                    farmerData,

                    password

                )

                .collect {

                    when (it) {

                        is ResultState.Loading -> {

                            _authState.value =

                                AuthState(
                                    isLoading = true
                                )
                        }

                        is ResultState.Success -> {

                            _authState.value =

                                AuthState(
                                    success = it.data
                                )
                        }

                        is ResultState.Error -> {

                            _authState.value =

                                AuthState(
                                    error = it.message
                                )
                        }
                    }
                }
        }
    }

    fun login(

        email: String,

        password: String

    ) {

        viewModelScope.launch {

            loginUseCase

                .login(

                    email,

                    password

                )

                .collect {

                    when (it) {

                        is ResultState.Loading -> {

                            _authState.value =

                                AuthState(
                                    isLoading = true
                                )
                        }

                        is ResultState.Success -> {

                            _authState.value =

                                AuthState(
                                    success = it.data
                                )
                        }

                        is ResultState.Error -> {

                            _authState.value =

                                AuthState(
                                    error = it.message
                                )
                        }
                    }
                }
        }
    }

    fun isUserLoggedIn(): Boolean {

        return isUserLoggedInUseCase()
    }

    fun logout() {

        logoutUseCase()
    }

    fun getRecommendations(

        district: String,

        month: Int,

        season: String,

        soilType: String

    ) {

        viewModelScope.launch {

            getRecommendedCropsUseCase(

                district,

                month,

                season,

                soilType

            ).collect {

                when (it) {

                    is ResultState.Loading -> {

                        _recommendationState.value =

                            RecommendationState(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {

                        _recommendationState.value =

                            RecommendationState(
                                crops = it.data
                            )
                    }

                    is ResultState.Error -> {

                        _recommendationState.value =

                            RecommendationState(
                                error = it.message
                            )
                    }
                }
            }
        }
    }

    fun getCropMethod(

        cropId: String

    ) {

        viewModelScope.launch {

            getCropMethodUseCase(

                cropId

            ).collect {

                when (it) {

                    is ResultState.Loading -> {

                        _cropMethodState.value =

                            CropMethodState(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {

                        _cropMethodState.value =

                            CropMethodState(
                                cropMethod = it.data
                            )
                    }

                    is ResultState.Error -> {

                        _cropMethodState.value =

                            CropMethodState(
                                error = it.message
                            )
                    }
                }
            }
        }
    }

    fun createFarm(

        farm: Farm

    ) {

        viewModelScope.launch {

            createFarmUseCase(

                farm

            ).collect {

                when (it) {

                    is ResultState.Loading -> {

                        _createFarmState.value =

                            CreateFarmState(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {

                        _createFarmState.value =

                            CreateFarmState(
                                success = it.data
                            )
                    }

                    is ResultState.Error -> {

                        _createFarmState.value =

                            CreateFarmState(
                                error = it.message
                            )
                    }
                }
            }
        }
    }

    fun getMyFarms() {

        viewModelScope.launch {

            getMyFarmsUseCase()

                .collect {

                    when (it) {

                        is ResultState.Loading -> {

                            _myFarmsState.value =

                                MyFarmsState(
                                    isLoading = true
                                )
                        }

                        is ResultState.Success -> {

                            _myFarmsState.value =

                                MyFarmsState(
                                    farms = it.data
                                )
                        }

                        is ResultState.Error -> {

                            _myFarmsState.value =

                                MyFarmsState(
                                    error = it.message
                                )
                        }
                    }
                }
        }
    }

    fun getCropSchedule(

        cropId: String

    ) {

        viewModelScope.launch {

            getCropScheduleUseCase(

                cropId

            ).collect {

                when (it) {

                    is ResultState.Loading -> {

                        _cropScheduleState.value =

                            CropScheduleState(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {

                        _cropScheduleState.value =

                            CropScheduleState(
                                schedule = it.data
                            )
                    }

                    is ResultState.Error -> {

                        _cropScheduleState.value =

                            CropScheduleState(
                                error = it.message
                            )
                    }
                }
            }
        }
    }

    fun getFarmById(

        farmId: String

    ) {

        viewModelScope.launch {

            getFarmByIdUseCase(
                farmId
            ).collect {

                when (it) {

                    is ResultState.Loading -> {

                        _farmDetailsState.value =
                            FarmDetailsState(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {

                        _farmDetailsState.value =
                            FarmDetailsState(
                                farm = it.data
                            )
                    }

                    is ResultState.Error -> {

                        _farmDetailsState.value =
                            FarmDetailsState(
                                error = it.message
                            )
                    }
                }
            }
        }
    }

    fun updateTaskStatus(

        farmId: String,

        task: TaskItem,

        completed: Boolean

    ) {

        viewModelScope.launch {

            updateTaskStatusUseCase(

                farmId,

                task,

                completed

            ).collect { result ->

                when (result) {

                    is ResultState.Loading -> {

                        _taskUpdateState.value =

                            TaskUpdateState(
                                isLoading = true
                            )

                    }

                    is ResultState.Success -> {

                        _taskUpdateState.value =

                            TaskUpdateState(
                                success = result.data
                            )

                    }

                    is ResultState.Error -> {

                        _taskUpdateState.value =

                            TaskUpdateState(
                                error = result.message
                            )

                    }

                }

            }

        }

    }

    fun getCompletedTasks(

        farmId: String

    ) {

        viewModelScope.launch {

            getCompletedTasksUseCase(

                farmId

            ).collect { result ->

                when (result) {

                    is ResultState.Loading -> {

                        _completedTasksState.value =

                            CompletedTasksState(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {

                        _completedTasksState.value =

                            CompletedTasksState(

                                completedTasks = result.data

                            )

                    }

                    is ResultState.Error -> {

                        _completedTasksState.value =

                            CompletedTasksState(
                                error = result.message
                            )
                    }
                }
            }
        }
    }

    fun saveDailyUpdate(

        farmId: String,

        update: DailyFarmUpdate,

        imageUri: Uri?

    ) {

        viewModelScope.launch {

            saveDailyUpdateUseCase(

                farmId,

                update,

                imageUri

            ).collect {

                when (it) {

                    is ResultState.Loading -> {

                        _saveDailyUpdateState.value =
                            SaveDailyUpdateState(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {

                        _saveDailyUpdateState.value =
                            SaveDailyUpdateState(
                                success = it.data
                            )
                    }

                    is ResultState.Error -> {

                        _saveDailyUpdateState.value =
                            SaveDailyUpdateState(
                                error = it.message
                            )
                    }
                }
            }
        }
    }

    fun getDailyUpdates(

        farmId: String

    ) {

        viewModelScope.launch {

            getDailyUpdatesUseCase(

                farmId

            ).collect {

                when (it) {

                    is ResultState.Loading -> {

                        _cropHealthHistoryState.value =

                            CropHealthHistoryState(
                                isLoading = true
                            )
                    }

                    is ResultState.Success -> {

                        _cropHealthHistoryState.value =

                            CropHealthHistoryState(
                                updates = it.data
                            )
                    }

                    is ResultState.Error -> {

                        _cropHealthHistoryState.value =

                            CropHealthHistoryState(
                                error = it.message
                            )
                    }
                }
            }
        }
    }

    fun getLatestAIStatus(

        farmId: String

    ) {

        viewModelScope.launch {

            getLatestAIStatusUseCase(

                farmId

            ).collect { result ->

                when (result) {

                    is ResultState.Loading -> {

                        _latestAIState.value =

                            LatestAIState(

                                isLoading = true

                            )

                    }

                    is ResultState.Success -> {

                        _latestAIState.value =

                            LatestAIState(

                                update = result.data

                            )

                    }

                    is ResultState.Error -> {

                        _latestAIState.value =

                            LatestAIState(

                                error = result.message

                            )

                    }

                }

            }

        }

    }
    fun getCropHistory(

        farmId: String

    ) {

        viewModelScope.launch {

            getDailyUpdatesUseCase(

                farmId

            ).collect { result ->

                when (result) {

                    is ResultState.Loading -> {

                        _cropHistoryState.value =

                            CropHistoryState(

                                isLoading = true

                            )

                    }

                    is ResultState.Success -> {

                        _cropHistoryState.value =

                            CropHistoryState(

                                updates = result.data
                                    .sortedByDescending { it.day }

                            )

                    }

                    is ResultState.Error -> {

                        _cropHistoryState.value =

                            CropHistoryState(

                                error = result.message

                            )

                    }

                }

            }

        }

    }

    fun addExpense(

        farmId: String,

        expense: Expense

    ) {

        viewModelScope.launch {

            addExpenseUseCase(

                farmId,

                expense

            ).collect { result ->

                when(result){

                    is ResultState.Loading -> {

                        _addExpenseState.value =

                            AddExpenseState(

                                isLoading = true

                            )

                    }

                    is ResultState.Success -> {

                        _addExpenseState.value =

                            AddExpenseState(

                                success = result.data

                            )

                    }

                    is ResultState.Error -> {

                        _addExpenseState.value =

                            AddExpenseState(

                                error = result.message

                            )

                    }

                }

            }

        }

    }

    fun getExpenses(

        farmId: String

    ) {

        viewModelScope.launch {

            getExpensesUseCase(

                farmId

            ).collect { result ->

                when(result){

                    is ResultState.Loading -> {

                        _expenseListState.value =

                            ExpenseListState(

                                isLoading = true

                            )

                    }

                    is ResultState.Success -> {

                        _expenseListState.value =

                            ExpenseListState(

                                expenses = result.data

                            )

                    }

                    is ResultState.Error -> {

                        _expenseListState.value =

                            ExpenseListState(

                                error = result.message

                            )

                    }

                }

            }

        }

    }

}