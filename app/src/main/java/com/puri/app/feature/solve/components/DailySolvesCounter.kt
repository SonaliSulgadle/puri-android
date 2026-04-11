package com.puri.app.feature.solve.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.core.ui.theme.SurfaceContainerLow

private const val DAILY_LIMIT = 10

@Composable
fun DailySolvesCounter(
    remaining: Int,
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
        LinearProgressIndicator(
            progress = { remaining.toFloat() / DAILY_LIMIT },
            modifier = Modifier
                .width(80.dp)
                .height(dimensionResource(R.dimen.progress_bar_height))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_pill))),
            color = IndigoPrimary,
            trackColor = SurfaceContainerLow
        )
        Text(
            text = "$remaining/$DAILY_LIMIT",
            style = MaterialTheme.typography.labelMedium,
            color = IndigoPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DailySolvesCounterPreview() {
    PuriTheme {
        DailySolvesCounter(
            remaining = 7,
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_xl))
        )
    }
}