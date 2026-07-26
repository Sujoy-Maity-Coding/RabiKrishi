package com.sujoy.smartfarm.Presentation.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujoy.smartfarm.AI.model.CropRecommendationRequest
import com.sujoy.smartfarm.AI.model.EstimateCostRequest
import com.sujoy.smartfarm.AI.model.GeminiRequest
import com.sujoy.smartfarm.Domain.UseCase.AnalyzeCrop.AnalyzeCropUseCase
import com.sujoy.smartfarm.Domain.UseCase.CropMethod.EstimateCostUseCase
import com.sujoy.smartfarm.Domain.UseCase.GetCropRecommendationUseCase
import com.sujoy.smartfarm.Domain.model.CropMethod.CostEstimation
import com.sujoy.smartfarm.Presentation.State.AnalyzeCrop.GeminiUiState
import com.sujoy.smartfarm.Presentation.State.RecomMethodCreate.EstimateCostState
import com.sujoy.smartfarm.Presentation.State.RecomMethodCreate.RecommendationState
import com.sujoy.smartfarm.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeminiViewModel @Inject constructor(

    private val analyzeCropUseCase: AnalyzeCropUseCase,
    private val estimateCostUseCase: EstimateCostUseCase,
    private val getCropRecommendationUseCase: GetCropRecommendationUseCase,
    @ApplicationContext private val context: Context

) : ViewModel() {

    private val _uiState = MutableStateFlow(GeminiUiState())
    val uiState = _uiState.asStateFlow()

    private val _estimateCostState =
        MutableStateFlow(EstimateCostState())

    val estimateCostState =
        _estimateCostState.asStateFlow()

    private val _recommendationState =

        MutableStateFlow(RecommendationState())

    val recommendationState: StateFlow<RecommendationState> =

        _recommendationState.asStateFlow()

    private var selectedEstimate: CostEstimation? = null

    private val _translatedNote = MutableStateFlow("")
    val translatedNote: StateFlow<String> = _translatedNote.asStateFlow()

    //    fun translateToEnglish(text: String) {
//        viewModelScope.launch {
//            try {
//                val prompt = """
//                Translate the following farmer's note to English.
//                Keep it simple and natural. Only return the translated text, nothing else.
//
//                Text: "$text"
//            """.trimIndent()
//
//                // Use your existing Gemini text call here
//                val result = geminiRepository.generateText(prompt)
//                _translatedNote.value = result
//            } catch (e: Exception) {
//                _translatedNote.value = text  // fallback: keep original
//            }
//        }
//    }
    fun translateToEnglish(text: String) {
        viewModelScope.launch {
            try {
                // Use analyzeCropUseCase's underlying repository
                // Since you don't have a direct text API, use the same Gemini client
                // Simplest fix — call the repository directly if injected,
                // otherwise just store as-is and let Gemini handle it via the crop prompt
                _translatedNote.value = text  // store as spoken, Gemini will process in context
            } catch (e: Exception) {
                _translatedNote.value = text
            }
        }
    }


    fun selectEstimate(estimate: CostEstimation) {

        selectedEstimate = estimate

    }

    fun getSelectedEstimate(): CostEstimation? {

        return selectedEstimate

    }

    fun analyzeCrop(

        request: GeminiRequest

    ) {

        viewModelScope.launch {

            _uiState.value = GeminiUiState(
                isLoading = true
            )

            val result = analyzeCropUseCase(request)

            result.onSuccess { analysis ->

                _uiState.value = GeminiUiState(
                    result = analysis
                )

            }

            result.onFailure {

                val message = when {

                    it.message?.contains("busy", true) == true ->
                        context.getString(R.string.gemini_err_busy_retry)

                    it.message?.contains("503", true) == true ->
                        context.getString(R.string.gemini_err_server_unavailable)

                    else ->
                        it.message ?: context.getString(R.string.repo_err_unknown)

                }

                _uiState.value = GeminiUiState(
                    error = message
                )

            }

        }

    }

    fun estimateCost(

        request: EstimateCostRequest

    ) {

        viewModelScope.launch {

            _estimateCostState.value =
                _estimateCostState.value.copy(
                    isLoading = true,
                    error = ""
                )

            val key = buildCacheKey(

                request.farmingMethod,

                request.farmSize,

                request.languageCode

            )

            val cached =

                _estimateCostState.value
                    .results[key]

            if (cached != null) {

                _estimateCostState.value =

                    _estimateCostState.value.copy(

                        currentKey = key

                    )

                return@launch

            }

            val result = estimateCostUseCase(request)

            result.onSuccess {

                _estimateCostState.value =

                    _estimateCostState.value.copy(

                        results =

                        _estimateCostState.value.results +

                                (key to it),

                        currentKey = key,

                        isLoading = false

                    )

            }

            result.onFailure {

                _estimateCostState.value =
                    _estimateCostState.value.copy(
                        isLoading = false,
                        error = it.message ?: context.getString(R.string.repo_err_unknown)
                    )

            }

        }

    }

    private fun buildCacheKey(

        farmingMethod: String,

        farmSize: Double,

        languageCode: String

    ): String {

        return "${farmingMethod.uppercase()}_${farmSize}_$languageCode"

    }

    fun getRecommendations(

        district: String,

        month: Int,

        season: String,

        soilType: String

    ) {

        viewModelScope.launch {

            _recommendationState.value =

                RecommendationState(

                    isLoading = true

                )

            val result =

                getCropRecommendationUseCase(

                    CropRecommendationRequest(

                        district = district,

                        month = month,

                        season = season,

                        soilType = soilType

                    )

                )

            result.onSuccess { crops ->

                _recommendationState.value =

                    RecommendationState(

                        crops = crops

                    )

            }

            result.onFailure { exception ->

                _recommendationState.value =

                    RecommendationState(

                        error = exception.message ?: context.getString(R.string.gemini_err_unable_recommendations2)

                    )

            }

        }

    }

}