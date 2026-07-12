package com.sujoy.smartfarm.AI.gemini

import com.sujoy.smartfarm.AI.model.EstimateCostRequest

object EstimateCostPrompt {

    fun build(

        request: EstimateCostRequest

    ): String {

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

1. Cost must be in Indian Rupees.

2. Yield should match the farm size.

3. Duration should be in days.

4. Labour should be estimated.

5. Materials should include seed, fertilizer, pesticide, compost, bio-fertilizer, irrigation or any required material.

6. Do not return markdown.

7. Do not explain anything.

8. Return JSON only.

        """.trimIndent()

    }

}