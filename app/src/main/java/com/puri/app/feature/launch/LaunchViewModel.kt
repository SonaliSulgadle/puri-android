package com.puri.app.feature.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puri.app.domain.usecase.InitAnonymousAuthUseCase
import com.puri.app.domain.usecase.launch.GetFirstLaunchUseCase
import com.puri.app.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    getFirstLaunchUseCase: GetFirstLaunchUseCase,
    private val initAuth: InitAnonymousAuthUseCase
) : ViewModel() {

    val startDestination: StateFlow<String?> = getFirstLaunchUseCase()
        .map { isFirst ->
            if (isFirst) Screen.Onboarding.route else Screen.Home.route
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            initAuth()
        }
    }
}