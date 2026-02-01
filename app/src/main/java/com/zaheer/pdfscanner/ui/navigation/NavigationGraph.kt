package com.zaheer.pdfscanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zaheer.pdfscanner.ui.screens.CameraScreen
import com.zaheer.pdfscanner.ui.screens.PreviewScreen
import com.zaheer.pdfscanner.ui.screens.ProScreen
import com.zaheer.pdfscanner.ui.screens.ResultScreen
import com.zaheer.pdfscanner.ui.viewmodel.ScannerViewModel

@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: ScannerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Camera.route
    ) {
        composable(Screen.Camera.route) {
            CameraScreen(
                viewModel = viewModel,
                onNavigateToPreview = { navController.navigate(Screen.Preview.route) },
                onNavigateToPro = { navController.navigate(Screen.Pro.route) }
            )
        }
        
        composable(Screen.Preview.route) {
            PreviewScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { navController.navigate(Screen.Result.route) }
            )
        }
        
        composable(Screen.Result.route) {
            ResultScreen(
                viewModel = viewModel,
                onNavigateToCamera = { 
                    navController.popBackStack(Screen.Camera.route, inclusive = false)
                }
            )
        }
        
        composable(Screen.Pro.route) {
            ProScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
