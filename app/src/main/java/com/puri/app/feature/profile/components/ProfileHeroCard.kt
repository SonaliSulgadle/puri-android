package com.puri.app.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puri.app.R

@Composable
fun ProfileHeroCard(
    appVersion: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_xl)))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1A1040),
                        Color(0xFF2D1B69),
                        Color(0xFF4D51B1)
                    )
                )
            )
    ) {
        // Decorative background text
        Text(
            text = stringResource(R.string.puri_brand_badge_label),
            fontSize = 100.sp,
            color = Color.White.copy(alpha = 0.05f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = dimensionResource(R.dimen.spacing_sm),
                    end = dimensionResource(R.dimen.spacing_lg)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_2xl)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.puri_brand_badge_label),
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = stringResource(R.string.profile_app_tagline),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            // Version badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "v$appVersion",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}