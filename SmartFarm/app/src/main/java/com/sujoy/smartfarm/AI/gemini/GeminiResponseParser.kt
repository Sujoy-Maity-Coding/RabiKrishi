package com.sujoy.smartfarm.AI.gemini

import com.google.gson.Gson
import com.sujoy.smartfarm.AI.model.GeminiAnalysisResult
import javax.inject.Inject
import javax.inject.Singleton

class GeminiResponseParser @Inject constructor() {

    private val gson = Gson()

    fun parse(

        response: String

    ): GeminiAnalysisResult {

        val cleanJson =

            response

                .replace("```json", "")

                .replace("```", "")

                .trim()

        return gson.fromJson(

            cleanJson,

            GeminiAnalysisResult::class.java

        )

    }

}