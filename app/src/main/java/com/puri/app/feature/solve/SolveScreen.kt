package com.puri.app.feature.solve

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.permission.PermissionManager
import com.puri.app.feature.solve.components.CameraScreen
import com.puri.app.feature.solve.components.DailyLimitCard
import com.puri.app.feature.solve.components.HomeContent
import com.puri.app.feature.solve.components.LoadingContent
import com.puri.app.feature.solve.components.ResponseCard
import com.puri.app.feature.solve.components.RetryCard
import com.puri.app.feature.solve.components.UnsafeContentCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SolveScreen(
    onNavigateToSaved: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: SolveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ── Permission launchers ───────────────────────────────────────────────
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.onIntent(SolveIntent.OpenCamera)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.permission_camera_denied)
                )
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(context.contentResolver, uri)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            }
            viewModel.onIntent(
                SolveIntent.GalleryImageSelected(bitmap, uri.toString())
            )
        }
    }

    // ── Effect collection ──────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is SolveUiEffect.ShowSnackbar -> scope.launch {
                    snackbarHostState.showSnackbar(context.getString(effect.messageRes))
                }

                SolveUiEffect.ShowSaveConfirmation -> scope.launch {
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.solve_saved_confirmation)
                    )
                }

                SolveUiEffect.NavigateToHistory -> onNavigateToHistory()
                SolveUiEffect.TriggerHaptic -> triggerHaptic(context)
            }
        }
    }

    // ── Camera open helper — called from HomeContent ───────────────────────
    val onOpenCamera: () -> Unit = {
        if (PermissionManager.hasCameraPermission(context)) {
            viewModel.onIntent(SolveIntent.OpenCamera)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val onOpenGallery: () -> Unit = {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    // ── State rendering ────────────────────────────────────────────────────
    when (val state = uiState) {
        is SolveUiState.Idle -> HomeContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onOpenCamera = onOpenCamera,
            onOpenGallery = onOpenGallery,
            onTextQueryChanged = { viewModel.onIntent(SolveIntent.TextQueryChanged(it)) },
            onSubmitQuery = { viewModel.onIntent(SolveIntent.SubmitTextQuery) },
            onViewAllHistory = onNavigateToHistory
        )

        SolveUiState.CameraOpen -> CameraScreen(
            onPhotoCaptured = { bitmap, uri ->
                viewModel.onIntent(SolveIntent.ImageCaptured(bitmap, uri))
            },
            onDismiss = { viewModel.onIntent(SolveIntent.ClearResult) }
        )

        is SolveUiState.Loading -> LoadingContent(bitmap = state.bitmap)
        is SolveUiState.Success -> ResponseCard(
            result = state.result,
            isSaved = state.isSaved,
            onSave = { viewModel.onIntent(SolveIntent.SaveResult) },
            onSolveAgain = { viewModel.onIntent(SolveIntent.ClearResult) }
        )

        is SolveUiState.Uncertain -> RetryCard(
            bitmap = state.bitmap,
            onRetry = { viewModel.onIntent(SolveIntent.Retry) },
            onUseSaved = onNavigateToSaved
        )

        SolveUiState.UnsafeContent -> UnsafeContentCard(
            onDismiss = { viewModel.onIntent(SolveIntent.ClearResult) },
            onCallEmergency = {
                context.startActivity(
                    Intent(Intent.ACTION_DIAL, "tel:119".toUri())
                )
            }
        )

        SolveUiState.DailyLimitReached -> DailyLimitCard(
            onViewHistory = onNavigateToHistory,
            onBrowseSaved = onNavigateToSaved
        )

        is SolveUiState.Error -> {
            LaunchedEffect(state) {
                snackbarHostState.showSnackbar(context.getString(R.string.error_unknown))
                viewModel.onIntent(SolveIntent.ClearResult)
            }
            HomeContent(
                state = SolveUiState.Idle(
                    dailySolvesRemaining = 10,
                    recentSolves = emptyList()
                ),
                snackbarHostState = snackbarHostState,
                onOpenCamera = onOpenCamera,
                onOpenGallery = onOpenGallery,
                onTextQueryChanged = { viewModel.onIntent(SolveIntent.TextQueryChanged(it)) },
                onSubmitQuery = { viewModel.onIntent(SolveIntent.SubmitTextQuery) },
                onViewAllHistory = onNavigateToHistory
            )
        }
    }
}

private fun triggerHaptic(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
            .defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    vibrator.vibrate(
        VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
    )
}