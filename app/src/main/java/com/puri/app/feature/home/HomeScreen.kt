package com.puri.app.feature.home

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToSolveResult: (Int) -> Unit
) {
    // TODO: Implement Home Screen
    Text(
        text = "Home Screen"
    )
}