package com.puri.app.feature.saved.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.domain.model.GuideSection
import com.puri.app.feature.saved.detail.SectionTitle

@Composable
fun TableSection(section: GuideSection) {
    Column {
        SectionTitle(text = section.title)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        Card(
            shape = RoundedCornerShape(dimensionResource(R.dimen.radius_lg)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column {
                section.items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.spacing_md)),
                        horizontalArrangement = Arrangement.spacedBy(
                            dimensionResource(R.dimen.spacing_md)
                        ),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Korean / label column
                        Column(modifier = Modifier.width(130.dp)) {
                            Text(
                                text = item.korean,
                                style = MaterialTheme.typography.bodyMedium,
                                color = IndigoPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            if (item.translation.isNotBlank()) {
                                Text(
                                    text = item.translation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        // Detail column
                        Text(
                            text = item.detail,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (index < section.items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(R.dimen.spacing_md)
                            ),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}