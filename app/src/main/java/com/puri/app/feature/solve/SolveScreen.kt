package com.puri.app.feature.solve

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.common.saveToTempFile
import com.puri.app.core.common.scaleToSafe
import com.puri.app.core.permission.PermissionManager
import com.puri.app.core.util.HapticUtils
import com.puri.app.feature.solve.SolveIntent.ImageCaptured
import com.puri.app.feature.solve.SolveIntent.TextQueryChanged
import com.puri.app.feature.solve.components.CameraScreen
import com.puri.app.feature.solve.components.DailyLimitCard
import com.puri.app.feature.solve.components.ErrorCard
import com.puri.app.feature.solve.components.GalleryContextSheet
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
    onNavigateToHistoryDetail: (Long) -> Unit,
    onOpenAddressConverter: () -> Unit,
    viewModel: SolveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val loadingType by viewModel.loadingType.collectAsStateWithLifecycle()

    var pendingGalleryBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showContextSheet by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.onIntent(SolveIntent.OpenCamera)
        else scope.launch {
            snackbarHost.showSnackbar(context.getString(R.string.permission_camera_denied))
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        scope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                val raw = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(context.contentResolver, uri)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                raw.scaleToSafe()
            }
            pendingGalleryBitmap = bitmap
            showContextSheet = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is SolveUiEffect.ShowSnackbar ->
                    scope.launch {
                        snackbarHost.showSnackbar(context.getString(effect.messageRes))
                    }

                SolveUiEffect.ShowSaveConfirmation ->
                    scope.launch {
                        snackbarHost.showSnackbar(
                            context.getString(R.string.solve_saved_confirmation)
                        )
                    }

                SolveUiEffect.NavigateToHistory -> onNavigateToHistory()
                SolveUiEffect.TriggerHaptic -> HapticUtils.triggerLight(context)
            }
        }
    }

    val onOpenCamera: () -> Unit = {
        if (PermissionManager.hasCameraPermission(context))
            viewModel.onIntent(SolveIntent.OpenCamera)
        else
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    val onOpenGallery: () -> Unit = {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    BackHandler(
        enabled = uiState !is SolveUiState.Idle
    ) {
        when (uiState) {
            SolveUiState.Loading -> {}

            else -> viewModel.onIntent(SolveIntent.ClearResult)
        }
    }

    if (showContextSheet) {
        GalleryContextSheet(
            onConfirm = { contextText ->
                showContextSheet = false
                val bitmap = pendingGalleryBitmap ?: return@GalleryContextSheet
                pendingGalleryBitmap = null
                scope.launch {
                    val uri = withContext(Dispatchers.IO) {
                        bitmap.saveToTempFile(context)?.toString()
                    }
                    if (!contextText.isNullOrBlank()) {
                        viewModel.onIntent(SolveIntent.AdditionalContextChanged(contextText))
                    }
                    viewModel.onIntent(ImageCaptured(bitmap, uri))
                }
            },
            onSkip = {
                showContextSheet = false
                val bitmap = pendingGalleryBitmap ?: return@GalleryContextSheet
                pendingGalleryBitmap = null
                scope.launch {
                    val uri = withContext(Dispatchers.IO) {
                        bitmap.saveToTempFile(context)?.toString()
                    }
                    viewModel.onIntent(ImageCaptured(bitmap, uri))
                }
            }
        )
    }

    AnimatedContent(
        targetState = uiState,
        modifier = Modifier.fillMaxSize(),
        contentKey = { state ->
            when (state) {
                is SolveUiState.Idle -> 0
                SolveUiState.CameraOpen -> 1
                SolveUiState.Loading -> 2
                is SolveUiState.Success -> 3
                is SolveUiState.Uncertain -> 4
                SolveUiState.UnsafeContent -> 5
                SolveUiState.DailyLimitReached -> 6
                is SolveUiState.Error -> 7
                is SolveUiState.AddressResult -> 8
            }
        },
        transitionSpec = {
            when {
                targetState is SolveUiState.Success ->
                    slideInVertically(tween(400)) { it / 3 } + fadeIn(tween(400)) togetherWith
                            fadeOut(tween(200))

                targetState is SolveUiState.Idle ->
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))

                targetState is SolveUiState.CameraOpen ->
                    slideInVertically(tween(350)) { it } + fadeIn(tween(350)) togetherWith
                            fadeOut(tween(200))

                targetState is SolveUiState.Loading ->
                    fadeIn(tween(150)) togetherWith fadeOut(tween(150))

                else ->
                    fadeIn(tween(250)) togetherWith fadeOut(tween(200))
            }
        },
        label = "solve_state_transition"
    ) { state ->
        when (state) {

            is SolveUiState.Idle ->
                HomeContent(
                    state = state,
                    snackbarHostState = snackbarHost,
                    onOpenCamera = onOpenCamera,
                    onOpenGallery = onOpenGallery,
                    onSubmitQuery = {
                        viewModel.onIntent(TextQueryChanged(it))
                        viewModel.onIntent(SolveIntent.SubmitTextQuery)
                    },
                    onViewAllHistory = onNavigateToHistory,
                    onNavigateToSaved = onNavigateToSaved,
                    onOpenAddressConverter = onOpenAddressConverter,
                    onNavigateToHistoryDetail = onNavigateToHistoryDetail
                )

            SolveUiState.Loading ->
                LoadingContent(
                    type = loadingType,
                    modifier = Modifier.fillMaxSize()
                )

            SolveUiState.CameraOpen ->
                CameraScreen(
                    onPhotoCaptured = { bitmap, additionalContext ->
                        scope.launch {
                            val safe = withContext(Dispatchers.Default) {
                                bitmap.scaleToSafe()
                            }
                            val uri = safe.saveToTempFile(context)?.toString()
                            if (!additionalContext.isNullOrBlank()) {
                                viewModel.onIntent(
                                    SolveIntent.AdditionalContextChanged(
                                        additionalContext
                                    )
                                )
                            }
                            viewModel.onIntent(ImageCaptured(safe, uri))
                        }
                    },
                    onDismiss = { viewModel.onIntent(SolveIntent.ClearResult) }
                )

            is SolveUiState.Success ->
                ResponseCard(
                    result = state.result,
                    isSaved = state.isSaved,
                    onSave = { viewModel.onIntent(SolveIntent.SaveResult) },
                    onSolveAgain = { viewModel.onIntent(SolveIntent.ClearResult) },
                    onBack = { viewModel.onIntent(SolveIntent.ClearResult) }
                )

            is SolveUiState.Uncertain ->
                RetryCard(
                    bitmap = state.bitmap,
                    onRetry = { viewModel.onIntent(SolveIntent.Retry) },
                    onUseSaved = onNavigateToSaved
                )

            SolveUiState.UnsafeContent ->
                UnsafeContentCard(
                    onDismiss = { viewModel.onIntent(SolveIntent.ClearResult) },
                    onCallEmergency = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, "tel:119".toUri())
                        )
                    }
                )

            SolveUiState.DailyLimitReached ->
                DailyLimitCard(
                    onViewHistory = onNavigateToHistory,
                    onBrowseSaved = onNavigateToSaved
                )

            is SolveUiState.Error -> {
                ErrorCard(
                    error = state.error,
                    onRetry = { viewModel.onIntent(SolveIntent.ClearResult) },
                    onDismiss = { viewModel.onIntent(SolveIntent.ClearResult) }
                )
            }

            is SolveUiState.AddressResult -> onOpenAddressConverter()
        }
    }
}