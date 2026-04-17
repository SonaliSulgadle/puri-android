package com.puri.app.feature.solve.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.domain.model.VisibleTextItem

@Composable
fun VisibleTextSection(
    visibleTexts: List<VisibleTextItem>,
    modifier: Modifier = Modifier
) {
    if (visibleTexts.isEmpty()) return

    Column(modifier = modifier) {
        SectionHeader(
            icon = Icons.Outlined.Translate,
            title = stringResource(R.string.response_visible_text)
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

        visibleTexts.forEach { item ->
            VisibleTextRow(item = item)
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
        }
    }
}

@Composable
private fun VisibleTextRow(
    item: VisibleTextItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_md)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_md)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = item.original,
                style = MaterialTheme.typography.bodyMedium,
                color = IndigoPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(110.dp)
            )
            Text(
                text = "→",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (item.explanation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VisibleTextSectionPreview() {
    PuriTheme {
        VisibleTextSection(
            visibleTexts = listOf(
                VisibleTextItem("표준 세탁", "Standard Wash", "everyday clothes, 40°C"),
                VisibleTextItem("탈수", "Spin Only", "no water, spin dry only")
            )
        )
    }
}