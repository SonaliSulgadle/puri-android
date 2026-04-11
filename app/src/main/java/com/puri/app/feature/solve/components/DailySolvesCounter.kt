package com.puri.app.feature.solve.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.core.ui.theme.PuriTheme

@Composable
fun DailySolvesCounter(
    remaining: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
    ) {
        Text(
            text = stringResource(R.string.home_daily_solves_label).uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        SolidProgressBar(
            progress = remaining.toFloat() / total,
            modifier = Modifier
                .width(80.dp)
                .height(dimensionResource(R.dimen.progress_bar_height)),
            progressColor = IndigoPrimary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )

        Text(
            text = "$remaining/$total",
            style = MaterialTheme.typography.labelMedium,
            color = IndigoPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@PreviewLightDark
@Composable
private fun DailySolvesCounterPreview() {
    PuriTheme {
        DailySolvesCounter(
            remaining = 7,
            total = 10,
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_xl))
        )
    }
}