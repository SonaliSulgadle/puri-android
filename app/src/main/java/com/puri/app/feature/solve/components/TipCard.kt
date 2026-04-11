package com.puri.app.feature.solve.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.puri.app.core.ui.theme.PuriTheme

@Composable
fun TipCard(
    tip: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_lg)),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = stringResource(R.string.emoji_tip),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))
            Column {
                Text(
                    text = stringResource(R.string.response_tip_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TipCardPreview() {
    PuriTheme {
        TipCard(
            tip = "Look for the recycling triangle with number 4",
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_sm))
        )
    }
}