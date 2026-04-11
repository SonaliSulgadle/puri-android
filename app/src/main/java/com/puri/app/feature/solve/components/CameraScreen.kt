package com.puri.app.feature.solve.components

import android.graphics.Bitmap
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
import kotlinx.coroutines.launch

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

    val cameraManager = remember { CameraManager(context) }
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        cameraManager.startCamera(lifecycleOwner, previewView)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── Camera viewfinder ──────────────────────────────────────────
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // ── Top gradient for control visibility ───────────────────────
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

        // ── Close button ───────────────────────────────────────────────
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

        // ── Hint ───────────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.camera_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = dimensionResource(R.dimen.spacing_xl) * 2)
        )

        // ── Bottom controls ────────────────────────────────────────────
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
                        scope.launch {
                            try {
                                isCapturing = true
                                val bitmap = cameraManager.capturePhoto()
                                onPhotoCaptured(
                                    bitmap,
                                    additionalContext.ifBlank { null }
                                )
                            } finally {
                                isCapturing = false
                                onDismiss()
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