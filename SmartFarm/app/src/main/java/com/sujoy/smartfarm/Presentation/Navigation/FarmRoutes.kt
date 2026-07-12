package com.sujoy.smartfarm.Presentation.Navigation

import com.sujoy.smartfarm.Domain.model.CropMethod.MaterialItem
import kotlinx.serialization.Serializable

sealed class FarmerRoutes {

    @Serializable
    object SplashScreen : FarmerRoutes()

    @Serializable
    object LoginScreen : FarmerRoutes()

    @Serializable
    object SignUpScreen : FarmerRoutes()

    @Serializable
    object HomeScreen : FarmerRoutes()

    @Serializable
    object CropRecommendationScreen : FarmerRoutes()

    @Serializable
    object ProfileScreen : FarmerRoutes()

    @Serializable
    data class RecommendationResultScreen(

        val district: String,

        val month: Int,

        val season: String,

        val soilType: String

    ) : FarmerRoutes()

    @Serializable
    data class MethodSelectionScreen(
        val cropId: String,
        val cropName: String
    ) : FarmerRoutes()

    @Serializable
    data class CreateFarmScreen(

        val cropId: String,

        val cropName: String,

        val farmingMethod: String,

        val farmSize: Double,

        val estimatedCost: String,

        val estimatedYield: String,

        val estimatedDuration: String,

        val labourRequired: String,

        val notes: String
    ) : FarmerRoutes()

    @Serializable
    object MyFarmsScreen : FarmerRoutes()

    @Serializable
    data class FarmDetailsScreen(

        val farmId: String,

        val cropId: String

    ) : FarmerRoutes()

    @Serializable
    data class CropUpdateScreen(

        val farmId: String,

        val currentDay: Int

    ) : FarmerRoutes()

    @Serializable
    data class CropHealthHistoryScreen(

        val farmId: String

    ) : FarmerRoutes()

    @Serializable
    data class CropUpdateDetailsScreen(

        val farmId: String,

        val updateId: String

    ) : FarmerRoutes()

    @Serializable
    data class FarmExpenseScreen(

        val farmId: String

    ) : FarmerRoutes()
}