package com.zaheer.pdfscanner.ui.navigation

sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object Preview : Screen("preview")
    object Result : Screen("result")
    object Pro : Screen("pro")
}
