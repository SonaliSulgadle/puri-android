package com.puri.app.feature.saved.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puri.app.R
import com.puri.app.core.ui.theme.CeramicWhite

@Composable
fun GuideCollectionBanner(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_xl)))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF192B20),
                        Color(0xFF3E5C49),
                        Color(0xFF5C8A6E)
                    )
                )
            )
            .padding(dimensionResource(R.dimen.spacing_xl))
    ) {
        // Decorative emoji
        Text(
            text = stringResource(R.string.korea_flag_emoji),
            fontSize = 80.sp,
            color = CeramicWhite.copy(alpha = 0.08f),
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        Column {
            // Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(CeramicWhite.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = dimensionResource(R.dimen.spacing_xs))
            ) {
                Text(
                    text = stringResource(R.string.guide_offline_ready_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = CeramicWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

            Text(
                text = stringResource(R.string.seoul_life_guides_label),
                style = MaterialTheme.typography.headlineSmall,
                color = CeramicWhite,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))

            Text(
                text = stringResource(R.string.essential_guides_info),
                style = MaterialTheme.typography.bodySmall,
                color = CeramicWhite.copy(alpha = 0.7f)
            )
        }
    }
}