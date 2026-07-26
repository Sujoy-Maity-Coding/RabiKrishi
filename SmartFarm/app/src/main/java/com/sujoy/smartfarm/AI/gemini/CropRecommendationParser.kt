package com.sujoy.smartfarm.AI.gemini

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.sujoy.smartfarm.Domain.model.Crop
import javax.inject.Inject

class CropRecommendationParser @Inject constructor() {

    private val gson = Gson()

    fun parse(response: String): List<Crop> {

        return try {

            val cleaned = response
                .replace("```json", "")
                .replace("```", "")
                .trim()

            gson.fromJson(
                cleaned,
                RecommendationResponse::class.java
            ).recommendations

        } catch (e: Exception) {

            emptyList()

        }

    }

}

data class RecommendationResponse(

    @SerializedName("recommendations")
    val recommendations: List<Crop> = emptyList()

)