package com.sujoy.smartfarm.AI.gemini

import com.sujoy.smartfarm.AI.model.GeminiRequest

class GeminiPromptBuilder {

    private fun languageName(code: String) = when (code) {
        "bn" -> "Bengali (বাংলা)"
        "hi" -> "Hindi (हिन्दी)"
        else -> "English"
    }

    fun buildPrompt(

        request: GeminiRequest

    ): String {

        val lang = languageName(request.languageCode)
        val isNonEnglish = request.languageCode != "en"

        val languageInstruction = if (isNonEnglish) {
            """
CRITICAL LANGUAGE REQUIREMENT:
Write every text field ENTIRELY in $lang script — this includes "diseaseName",
"currentPhase", "organicTreatment", "chemicalTreatment", "recommendedMedicine",
"medicineQuantity", "irrigationAdvice", "fertilizerAdvice", "weatherWarning", and every
string inside "todayTasks", "extraTasks", "postponeTasks", "cancelTasks", and "preventiveTips".
Do NOT mix scripts within a single field.
Keep "priority" as one of the exact English tokens LOW / NORMAL / HIGH / CRITICAL.
Keep "riskLevel" as one of the exact English tokens Healthy / Low / Medium / High / Critical.
Keep "severity" as one of the exact English tokens Mild / Moderate / Severe.
These three fields are status codes the app uses for logic and color-coding, not display text.
            """.trimIndent()
        } else {
            "Write every text field in clear English."
        }

        return """

You are an expert Agricultural Scientist specializing in crop health diagnostics
across all crop types.

Analyze the uploaded ${request.cropName} leaf image together with the farmer's observations.

IMPORTANT RULES

1. Analyze ONLY the crop named "${request.cropName}". Base your diagnosis on
   diseases, pests, and growth patterns relevant to this specific crop.
2. Use BOTH the uploaded image and the farmer's information.
3. Do NOT guess if information is unavailable.
4. Return ONLY valid JSON.
5. Do NOT return Markdown.
6. Do NOT explain anything outside the JSON.
7. $languageInstruction

--------------------------------------------------

FARM DETAILS

Crop Name:
${request.cropName}

Current Day:
${request.currentDay}

Farming Method:
${request.farmingMethod}

Field Area:
${request.fieldArea} Acre

District:
${request.district}

Season:
${request.season}

--------------------------------------------------

FARMER OBSERVATION

Plant Height:
${request.plantHeight} cm

Leaf Color:
${request.leafColor}

Soil Moisture:
${request.soilMoisture}

Pest Found:
${request.pestFound}

Flowering Started:
${request.floweringStarted}

Fruit Started:
${request.fruitStarted}

Farmer Note:
${request.farmerNote}

--------------------------------------------------

Analyze the crop and generate:

- Disease Name
- Confidence (0-100)
- Severity
- Health Score (0-100)
- Current Crop Phase
- Crop Progress Percentage
- Risk Level
- Organic Treatment
- Chemical Treatment
- Recommended Medicine
- Medicine Quantity
- Irrigation Advice
- Fertilizer Advice
- Today's Tasks
- Extra Tasks (new tasks that should be added)
- Postpone Tasks (planned tasks that should be delayed)
- Cancel Tasks (planned tasks that should not be performed now)
- Priority (LOW / NORMAL / HIGH / CRITICAL)
- Preventive Tips
- Weather Warning
- Next Inspection Days

Return ONLY this JSON:

{
  "diseaseName":"",
  "confidence":0,
  "severity":"",
  "healthScore":0,
  "currentPhase":"",
  "progress":0,
  "riskLevel":"",

  "organicTreatment":"",
  "chemicalTreatment":"",
  "recommendedMedicine":"",
  "medicineQuantity":"",

  "irrigationAdvice":"",
  "fertilizerAdvice":"",

  "todayTasks":[
    ""
  ],

  "extraTasks":[
    ""
  ],

  "postponeTasks":[
    ""
  ],

  "cancelTasks":[
    ""
  ],

  "priority":"NORMAL",

  "preventiveTips":[
    ""
  ],

  "weatherWarning":"",

  "nextInspectionDays":0
}

""".trimIndent()

    }

}