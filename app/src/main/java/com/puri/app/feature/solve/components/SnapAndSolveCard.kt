package com.puri.app.feature.solve.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.puri.app.R
import com.puri.app.core.ui.theme.GradientHeroEnd
import com.puri.app.core.ui.theme.GradientHeroStart
import com.puri.app.core.ui.theme.PuriTheme

@Composable
fun SnapAndSolveCard(
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_xl)))
            .background(Brush.linearGradient(listOf(GradientHeroStart, GradientHeroEnd)))
            .padding(dimensionResource(R.dimen.spacing_3xl)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.camera_icon_container))
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(dimensionResource(R.dimen.snap_solve_icon_size))
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            Text(
                text = stringResource(R.string.home_snap_solve_title),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

            Text(
                text = stringResource(R.string.home_snap_solve_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            // Primary — open camera
            Button(
                onClick = onCameraClick,
                shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = GradientHeroStart
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_sm))
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))
                Text(
                    text = stringResource(R.string.home_open_camera),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

            // Secondary — pick from gallery
            OutlinedButton(
                onClick = onGalleryClick,
                shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = dimensionResource(R.dimen.spacing_xs) / 4,
                    color = Color.White.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_sm))
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))
                Text(stringResource(R.string.home_choose_gallery))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SnapAndSolveCardPreview() {
    PuriTheme { SnapAndSolveCard(onCameraClick = {}, onGalleryClick = {}) }
}