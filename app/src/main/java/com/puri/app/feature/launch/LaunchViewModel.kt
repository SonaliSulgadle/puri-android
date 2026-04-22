package com.puri.app.feature.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.domain.usecase.launch.GetFirstLaunchUseCase
import com.puri.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    getFirstLaunchUseCase: GetFirstLaunchUseCase
) : ViewModel() {

    val startDestination: StateFlow<String?> = getFirstLaunchUseCase()
        .map { isFirst ->
            Screen.Onboarding.route
//            if (isFirst) Screen.Onboarding.route else Screen.Home.route
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}