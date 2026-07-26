package com.sujoy.smartfarm.AI.gemini

import com.sujoy.smartfarm.AI.model.CropRecommendationRequest

object CropRecommendationPrompt {

    fun build(

        request: CropRecommendationRequest

    ): String {

        return """

You are an agricultural expert specializing in West Bengal agriculture.

A farmer has provided the following information.

District:
${request.district}

Month:
${request.month}

Season:
${request.season}

Soil Type:
${request.soilType}

Recommend only crops commonly cultivated in India, especially West Bengal.

Do not recommend exotic crops.

Recommend the best 4 or 5 crops from the following list.

Return exactly 5 recommendations.

Only choose from this list.

Rice
Potato
Jute
Mustard
Wheat
Maize
Sunflower
Groundnut
Sugarcane
Tomato
Brinjal
Onion
Cabbage
Cauliflower
Chilli

Choose the best 3 crops.

- cropName
- recommendationScore (0-100)
- category
- waterRequirement (Low, Medium, High)
- growthDuration (e.g. 120 days)
- marketDemand (Low, Medium, High)
- emoji (single emoji representing the crop)

Return ONLY valid JSON.

{
  "recommendations": [
    {
      "cropName": "Rice",
      "scientificName": "Oryza sativa",
      "season": "Kharif",
      "category": "Cereal",
      "waterRequirement": "High",
      "growthDuration": "120 days",
      "recommendationScore": 95,
      "expectedYield": "4–5 tons/acre",
      "marketDemand": "High",
      "description": "Suitable for the selected district, soil type, season and planting month.",
      "farmingTips": "Maintain proper irrigation and balanced fertilizer application."
    }
  ]
}

Rules:
- Return exactly 3 recommendations.
- recommendationScore must be between 0 and 100.
- Use realistic values for Indian agriculture, especially West Bengal.
- Do not include markdown.
- Do not include explanations outside the JSON.
- Return JSON only.

        """.trimIndent()

    }

}