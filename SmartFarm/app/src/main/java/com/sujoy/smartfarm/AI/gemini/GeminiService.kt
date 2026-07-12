package com.sujoy.smartfarm.AI.gemini

import android.content.Context
import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.sujoy.smartfarm.AI.model.EstimateCostRequest
import com.sujoy.smartfarm.AI.model.GeminiRequest
import com.sujoy.smartfarm.Common.Constant
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor(

    @ApplicationContext
    private val context: Context

) {

    private val model = GenerativeModel(

        modelName = "gemini-2.5-flash",

        apiKey = Constant.GEMINI_API_KEY

    )

    private val promptBuilder = GeminiPromptBuilder()
    private val estimateCostPrompt = EstimateCostPrompt

    suspend fun analyzeCrop(

        request: GeminiRequest

    ): String {

        val bitmap = context.contentResolver
            .openInputStream(request.imageUri)
            ?.use { BitmapFactory.decodeStream(it) }
            ?: throw Exception("Unable to read selected image.")

        val prompt = promptBuilder.buildPrompt(request)

        var lastException: Exception? = null

        repeat(3) { attempt ->

            try {

                val response = model.generateContent(

                    content {

                        image(bitmap)

                        text(prompt)

                    }

                )

                val text = response.text

                if (!text.isNullOrBlank()) {

                    return text

                }

                throw Exception("Gemini returned an empty response.")

            } catch (e: Exception) {

                lastException = e

                val message = e.message ?: ""

                // Retry only if Gemini server is busy
                if (
                    message.contains("503", true) ||
                    message.contains("UNAVAILABLE", true) ||
                    message.contains("high demand", true)
                ) {

                    if (attempt < 2) {

                        delay(3000)

                    }

                } else {

                    throw Exception(
                        "Analysis failed: ${message.ifBlank { "Unknown error" }}"
                    )

                }

            }

        }

        throw Exception(
            "Gemini AI is currently busy. Please try again after a few moments."
        )

    }

    suspend fun estimateCost(

        request: EstimateCostRequest

    ): String {

        val prompt = estimateCostPrompt.build(request)

        var lastException: Exception? = null

        repeat(3) { attempt ->

            try {

                val response =

                    model.generateContent(prompt)

                val text = response.text

                if (!text.isNullOrBlank()) {

                    return text

                }

                throw Exception("Empty response")

            } catch (e: Exception) {

                lastException = e

                if (
                    e.message?.contains("503", true) == true ||
                    e.message?.contains("UNAVAILABLE", true) == true
                ) {

                    if (attempt < 2)
                        delay(3000)

                } else {

                    throw e

                }

            }

        }

        throw lastException ?: Exception("Gemini Busy")

    }

}