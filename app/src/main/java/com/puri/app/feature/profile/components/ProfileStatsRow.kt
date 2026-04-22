package com.puri.app.feature.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary

@Composable
fun ProfileStatsRow(
    todaySolves: Int,
    dailyLimit: Int,
    totalSolves: Int,
    totalSaved: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
    ) {
        StatBlock(
            value = "$todaySolves/$dailyLimit",
            label = stringResource(R.string.profile_stat_today),
            emoji = "⚡",
            modifier = Modifier.weight(1f)
        )
        StatBlock(
            value = totalSolves.toString(),
            label = stringResource(R.string.profile_stat_total),
            emoji = "🔍",
            modifier = Modifier.weight(1f)
        )
        StatBlock(
            value = totalSaved.toString(),
            label = stringResource(R.string.profile_stat_saved),
            emoji = "🔖",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatBlock(
    value: String,
    label: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_xl)))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(dimensionResource(R.dimen.spacing_lg)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = IndigoPrimary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}