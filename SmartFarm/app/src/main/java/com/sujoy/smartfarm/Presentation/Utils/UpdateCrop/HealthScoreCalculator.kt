package com.sujoy.smartfarm.Presentation.Utils.UpdateCrop

object HealthScoreCalculator {

    fun calculate(

        leafColor: String,

        soilMoisture: String,

        pestFound: Boolean,

        diseaseFound: Boolean,

        floweringStarted: Boolean

    ): Int {

        var score = 100

        if (leafColor == "Light Green")
            score -= 10

        if (leafColor == "Yellow")
            score -= 25

        if (soilMoisture == "Dry")
            score -= 15

        if (soilMoisture == "Wet")
            score -= 10

        if (pestFound)
            score -= 30

        if (diseaseFound)
            score -= 35

        if (!floweringStarted)
            score -= 5

        return score.coerceIn(0, 100)
    }
}