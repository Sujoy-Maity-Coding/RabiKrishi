package com.sujoy.smartfarm.Presentation.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sujoy.smartfarm.Presentation.Screens.CropMethod.MethodSelectionScreen
import com.sujoy.smartfarm.Presentation.Screens.CropRecommend.CropRecommendationScreen
import com.sujoy.smartfarm.Presentation.Screens.CropRecommend.RecommendationResultScreen
import com.sujoy.smartfarm.Presentation.Screens.CropHistory.CropHistoryScreen
import com.sujoy.smartfarm.Presentation.Screens.CropUpdate.CropUpdateScreen
import com.sujoy.smartfarm.Presentation.Screens.Dashboard.HomeScreen
import com.sujoy.smartfarm.Presentation.Screens.ExpenseTracker.FarmExpenseScreen
import com.sujoy.smartfarm.Presentation.Screens.FarmList.CreateFarmScreen
import com.sujoy.smartfarm.Presentation.Screens.FarmList.FarmDetailsScreen
import com.sujoy.smartfarm.Presentation.Screens.FarmList.MyFarmsScreen
import com.sujoy.smartfarm.Presentation.Screens.SignUpLogin.LoginScreen
import com.sujoy.smartfarm.Presentation.Screens.SignUpLogin.SignUpScreen
import com.sujoy.smartfarm.Presentation.Screens.SignUpLogin.SplashScreen

@Composable
fun App(modifier: Modifier){
    val navController = rememberNavController()

    NavHost(

        navController = navController,

        startDestination =
        FarmerRoutes.SplashScreen

    ){
        composable<FarmerRoutes.SplashScreen> {

            SplashScreen(navController)
        }

        composable<FarmerRoutes.LoginScreen> {

            LoginScreen(navController)
        }

        composable<FarmerRoutes.SignUpScreen> {

            SignUpScreen(navController)
        }

        composable<FarmerRoutes.HomeScreen> {

            HomeScreen(navController)
        }

        composable<FarmerRoutes.CropRecommendationScreen> {

            CropRecommendationScreen(
                navController = navController
            )
        }

        composable<FarmerRoutes.RecommendationResultScreen> {

            val args =
                it.toRoute<FarmerRoutes.RecommendationResultScreen>()

            RecommendationResultScreen(

                district = args.district,

                month = args.month,

                season = args.season,

                soilType = args.soilType,

                navController = navController
            )
        }

        composable<
                FarmerRoutes
                .MethodSelectionScreen
                > {

            val args =

                it.toRoute<
                        FarmerRoutes
                        .MethodSelectionScreen
                        >()

            MethodSelectionScreen(

                cropId = args.cropId,

                cropName = args.cropName,

                navController = navController
            )
        }

        composable<
                FarmerRoutes.CreateFarmScreen
                > {

            val args =

                it.toRoute<
                        FarmerRoutes.CreateFarmScreen
                        >()

            CreateFarmScreen(

                cropId = args.cropId,

                cropName = args.cropName,

                farmingMethod = args.farmingMethod,

                farmSize = args.farmSize,

                estimatedCost = args.estimatedCost,

                estimatedYield = args.estimatedYield,

                estimatedDuration = args.estimatedDuration,

                labourRequired = args.labourRequired,

                notes = args.notes,

                navController = navController

            )
        }

        composable<
                FarmerRoutes.MyFarmsScreen
                > {

            MyFarmsScreen(
                navController =
                navController
            )
        }

        composable<
                FarmerRoutes.FarmDetailsScreen
                > {

            val args =

                it.toRoute<
                        FarmerRoutes.FarmDetailsScreen
                        >()

            FarmDetailsScreen(

                farmId = args.farmId,

                cropId = args.cropId,

                navController =
                navController
            )
        }

        composable<FarmerRoutes.CropUpdateScreen> {

            val args =
                it.toRoute<FarmerRoutes.CropUpdateScreen>()

            CropUpdateScreen(

                farmId = args.farmId,

                currentDay = args.currentDay,

                navController = navController
            )
        }

        composable<FarmerRoutes.CropHealthHistoryScreen> {

            val args =
                it.toRoute<FarmerRoutes.CropHealthHistoryScreen>()

            CropHistoryScreen(

                farmId = args.farmId,

                navController = navController

            )
        }

        composable<FarmerRoutes.FarmExpenseScreen> {

            val args =
                it.toRoute<FarmerRoutes.FarmExpenseScreen>()

            FarmExpenseScreen(

                farmId = args.farmId,

                navController = navController

            )

        }
    }
}