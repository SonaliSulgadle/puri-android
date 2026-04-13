package com.puri.app.feature.solve.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.puri.app.R
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.core.util.DateTimeUtils
import com.puri.app.domain.model.HistoryItem
import com.puri.app.util.TestFixtures
import kotlin.math.ceil

@Composable
fun RecentSolvesSection(
    recentSolves: List<HistoryItem>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardHeight = dimensionResource(R.dimen.recent_solve_card_height)
    val spacing = dimensionResource(R.dimen.spacing_md)
    val rows = ceil(recentSolves.size / 2.0).toInt()
    val gridHeight = (cardHeight * rows) + (spacing * (rows - 1).coerceAtLeast(0))

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.home_recent_solves),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.home_view_all),
                style = MaterialTheme.typography.labelMedium,
                color = IndigoPrimary,
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

        val rows = recentSolves.take(4).chunked(2)
        rows.forEachIndexed { rowIndex, rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.spacing_md)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { item ->
                    RecentSolveCard(
                        item = item,
                        modifier = Modifier.weight(1f)
                    )
                }
                // If only one item in last row, fill remaining space
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            if (rowIndex < rows.lastIndex) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
            }
        }
    }
}

@Composable
private fun RecentSolveCard(
    item: HistoryItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_xl)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            item.solveResult.imageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = stringResource(R.string.cd_solve_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(R.dimen.recent_solve_image_size))
                        .clip(
                            RoundedCornerShape(
                                topStart = dimensionResource(R.dimen.radius_xl),
                                topEnd = dimensionResource(R.dimen.radius_xl)
                            )
                        )
                )
            }
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.spacing_md))
            ) {
                CategoryChip(category = item.solveResult.category)
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
                Text(
                    text = item.solveResult.whatThisIs,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
                Text(
                    text = DateTimeUtils.formatRelativeTimestamp(item.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun RecentSolvesSectionPreview() {
    PuriTheme {
        RecentSolvesSection(
            recentSolves = listOf(
                TestFixtures.historyItemToday,
                TestFixtures.historyItemToday,
                TestFixtures.historyItemYesterday,
            ),
            onViewAll = {},
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_xl))
        )
    }
}