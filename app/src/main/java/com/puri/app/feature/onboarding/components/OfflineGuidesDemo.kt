package com.puri.app.feature.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.theme.CeramicWhite

@Composable
fun OfflineGuidesDemo(
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Offline badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(100.dp))
                .background(accentColor.copy(alpha = 0.2f))
                .padding(horizontal = dimensionResource(R.dimen.spacing_md), vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Text(
                text = stringResource(R.string.onboarding_offline_badge),
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Guide preview chips — 2 per row
        val guides = listOf(
            Pair("♻️", R.string.onboarding_guide_trash),
            Pair("🚇", R.string.onboarding_guide_subway),
            Pair("🫧", R.string.onboarding_guide_washing),
            Pair("🏥", R.string.onboarding_guide_medical),
        )

        guides.chunked(2).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { (emoji, labelRes) ->
                    OfflineGuideChip(
                        emoji = emoji,
                        labelRes = labelRes,
                        accentColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }

        // "+7 more" pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_lg)))
                .background(CeramicWhite.copy(alpha = 0.06f))
                .padding(vertical = dimensionResource(R.dimen.spacing_md)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.onboarding_guide_more),
                style = MaterialTheme.typography.labelMedium,
                color = CeramicWhite.copy(alpha = 0.55f),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
