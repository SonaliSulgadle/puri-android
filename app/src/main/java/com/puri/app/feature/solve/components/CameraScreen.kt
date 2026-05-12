package com.puri.app.feature.solve.components

import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.viewinterop.AndroidView
import com.puri.app.R
import com.puri.app.core.camera.CameraManager
import com.puri.app.core.common.fixRotationFromDisplay
import com.puri.app.core.common.scaleToSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CameraScreen(
    onPhotoCaptured: (Bitmap, String?) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var additionalContext by remember { mutableStateOf("") }
    var isCapturing by remember { mutableStateOf(false) }
    var captureError by remember { mutableStateOf<String?>(null) }

    val cameraManager = remember { CameraManager(context) }
    val previewView = remember { PreviewView(context) }

    // Start camera — restart if lifecycleOwner changes
    LaunchedEffect(lifecycleOwner) {
        try {
            cameraManager.startCamera(lifecycleOwner, previewView)
        } catch (e: Exception) {
            // Camera failed to start — notify and dismiss
            captureError = "Camera failed to start. Please try again."
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraManager.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.spacing_bottom_nav))
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(dimensionResource(R.dimen.spacing_lg))
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.camera_close),
                tint = Color.White
            )
        }

        Text(
            text = stringResource(R.string.camera_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = dimensionResource(R.dimen.spacing_xl) * 2)
        )

        // Show capture error as overlay if something went wrong
        captureError?.let { error ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(dimensionResource(R.dimen.spacing_xl))
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_xl)))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(dimensionResource(R.dimen.spacing_lg))
            ) {
                Text(
                    text = error,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(dimensionResource(R.dimen.spacing_xl)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = additionalContext,
                onValueChange = { additionalContext = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.camera_additional_context_hint),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            ShutterButton(
                isCapturing = isCapturing,
                onClick = {
                    if (!isCapturing) {
                        captureError = null
                        scope.launch {
                            isCapturing = true
                            try {
                                val bitmap = cameraManager.capturePhoto()

                                // Fix rotation on background thread
                                // CameraX sensors often return images rotated 90° when phone
                                // is held portrait — the sensor is physically landscape
                                val corrected = withContext(Dispatchers.Default) {
                                    bitmap.fixRotationFromDisplay(
                                        cameraManager.getSensorRotation()
                                    ).scaleToSafe()
                                }

                                onPhotoCaptured(
                                    corrected,
                                    additionalContext.ifBlank { null }
                                )
                                onDismiss()
                            } catch (e: ImageCaptureException) {
                                // CameraX-specific capture failure
                                // Common causes: camera in use by another app,
                                // insufficient storage, hardware error
                                captureError = when (e.imageCaptureError) {
                                    ImageCapture.ERROR_CAMERA_CLOSED ->
                                        context.getString(R.string.camera_error_closed)

                                    ImageCapture.ERROR_CAPTURE_FAILED ->
                                        context.getString(R.string.camera_error_capture_failed)

                                    ImageCapture.ERROR_FILE_IO ->
                                        context.getString(R.string.camera_error_storage)

                                    ImageCapture.ERROR_INVALID_CAMERA ->
                                        context.getString(R.string.camera_error_invalid)

                                    else ->
                                        context.getString(R.string.camera_error_generic)
                                }
                                isCapturing = false
                            } catch (e: Exception) {
                                // Unexpected errors — bitmap processing failure etc
                                captureError = context.getString(R.string.camera_error_generic)
                                isCapturing = false
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))
        }
    }
}

@Composable
private fun ShutterButton(
    isCapturing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isCapturing) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "shutter_scale"
    )
    Box(
        modifier = modifier
            .size(dimensionResource(R.dimen.camera_icon_container))
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(
                if (isCapturing) Color.White.copy(alpha = 0.5f)
                else Color.White.copy(alpha = 0.3f)
            )
            .clickable(
                enabled = !isCapturing,
                role = Role.Button,
                onClickLabel = stringResource(R.string.camera_capture)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.confidence_icon_container))
                .clip(CircleShape)
                .background(
                    if (isCapturing) Color.White.copy(alpha = 0.7f) else Color.White
                )
        )
    }
}