package com.sujoy.smartfarm.AI.gemini

import com.sujoy.smartfarm.AI.model.ScheduleGenerationRequest

object ScheduleGenerationPrompt {

    private fun languageName(code: String) = when (code) {
        "bn" -> "Bengali (বাংলা)"
        "hi" -> "Hindi (हिन्दी)"
        else -> "English"
    }

    fun build(request: ScheduleGenerationRequest): String {
        val lang = languageName(request.languageCode)
        val isNonEnglish = request.languageCode != "en"

        val languageInstruction = if (isNonEnglish) {
            """
CRITICAL LANGUAGE REQUIREMENT:
Write every "title" and "description" field ENTIRELY in $lang script.
Do not mix scripts within a single field. Keep chemical/brand names (e.g. "NPK", "DAP") as-is if there is no common $lang equivalent.
            """.trimIndent()
        } else {
            "Write every \"title\" and \"description\" field in clear English."
        }

        return """
You are an agricultural expert specializing in West Bengal agriculture.

Below is ONE PHASE from a VERIFIED base cultivation schedule for this crop (generic/inorganic approach):

${request.basePhaseJson}

Adapt ONLY this phase for the following farmer's context:

Crop: ${request.cropName}
Farming Method: ${request.farmingMethod}
District: ${request.district}
Season: ${request.season}
Farm Size: ${request.farmSize} ${request.unit}

STRICT RULES:

1. Keep the exact same "phaseId", "startDay", "endDay", "taskId", and "day" values as given. Do NOT add, remove, reorder, or renumber tasks.

2. Only rewrite "title"/"description" for tasks related to fertilizer, pesticide, seed treatment, or soil amendment inputs:
   - "Organic": replace chemical inputs with organic equivalents (compost, FYM, vermicompost, neem-based biopesticides, Trichoderma, Pseudomonas fluorescens, etc.), with realistic quantities.
   - "Inorganic": keep chemical fertilizer/pesticide tasks as given, refine wording only if needed.
   - "Mixed": use a balanced combination of chemical and organic inputs for these tasks.

3. For ALL other tasks (land prep, irrigation, transplanting, weeding, harvesting, etc.) — copy "title"/"description" EXACTLY as given. Do not reword, do not "improve" phrasing.

4. Wherever an input quantity is mentioned (seed, fertilizer, compost, pesticide, irrigation volume), scale the number in "description" to this farm's actual size: state the exact amount for ${request.farmSize} ${request.unit}, not a generic per-acre/per-hectare rate. Keep the math realistic.

5. Do not invent pesticide/fertilizer names that don't exist. Use only real, commonly available Indian agricultural inputs.

6. $languageInstruction

7. Return ONLY valid JSON, in exactly this shape — a SINGLE phase object, not an array, not wrapped in a schedule:

{
  "phaseId": "",
  "title": "",
  "startDay": 0,
  "endDay": 0,
  "tasks": [
    { "taskId": "", "day": 0, "title": "", "description": "" }
  ]
}

8. Do not include markdown, comments, or explanation outside the JSON.
9. Return JSON only.
        """.trimIndent()
    }
}