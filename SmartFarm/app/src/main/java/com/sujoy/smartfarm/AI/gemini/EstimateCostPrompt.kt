package com.sujoy.smartfarm.AI.gemini

import com.sujoy.smartfarm.AI.model.EstimateCostRequest

object EstimateCostPrompt {

    private fun languageName(code: String) = when (code) {
        "bn" -> "Bengali (বাংলা)"
        "hi" -> "Hindi (हिन्दी)"
        else -> "English"
    }

    fun build(request: EstimateCostRequest): String {
        val lang = languageName(request.languageCode)
        val isNonEnglish = request.languageCode != "en"

        val languageInstruction = if (isNonEnglish) {
            """
CRITICAL LANGUAGE REQUIREMENT:
You MUST write the "labourRequired", "notes", and every "materials[].name" value ENTIRELY in $lang script.
Do NOT use any English words in these fields, except brand names or chemical formula codes that have no common $lang equivalent (e.g. "NPK", "DAP").
Do NOT mix scripts within a single field.

Example of correct "labourRequired" in Bengali: "প্রায় ৯০ শ্রমদিবস প্রয়োজন, যার মধ্যে রয়েছে জমি প্রস্তুতি, রোপণ, নিড়ানি, সেচ, কাটা এবং মাড়াই।"
Example of correct "materials[].name" in Bengali: "ধান বীজ", "ইউরিয়া", "গোবর সার"
            """.trimIndent()
        } else {
            "Write \"labourRequired\", \"notes\", and \"materials[].name\" in clear English."
        }

        return """
You are an agriculture expert.

Estimate the farming cost for the following farm.

Crop:
${request.cropName}

Farming Method:
${request.farmingMethod}

Farm Size:
${request.farmSize} ${request.unit}

Return ONLY valid JSON.

{
  "estimatedCost":"",
  "estimatedYield":"",
  "estimatedDuration":"",
  "labourRequired":"",
  "notes":"",
  "materials":[
      {
         "name":"",
         "quantity":""
      }
  ]
}

Rules:

1. Cost must be in Indian Rupees, as a plain number (no words, no currency symbol).

2. Yield should match the farm size, as a plain number with unit abbreviation in English (e.g. "4-5 tons/acre").

3. estimatedDuration must be a plain number of days only (e.g. "45"), no words.

4. $languageInstruction

5. Materials should include seed, fertilizer, pesticide, compost, bio-fertilizer, irrigation or any required material. Keep "materials[].quantity" as a plain number with unit (e.g. "50 kg").

6. Do not return markdown.

7. Do not explain anything.

8. Return JSON only.

        """.trimIndent()
    }
}