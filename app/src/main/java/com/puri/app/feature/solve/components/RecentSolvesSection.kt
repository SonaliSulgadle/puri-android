package com.puri.app.feature.solve.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.puri.app.R
import com.puri.app.core.ui.mapper.toChipColor
import com.puri.app.core.ui.theme.CeladonPrimary
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.core.util.DateTimeUtils
import com.puri.app.domain.model.HistoryItem
import com.puri.app.util.TestFixtures

@Composable
fun RecentSolvesSection(
    recentSolves: List<HistoryItem>,
    onViewAll: () -> Unit,
    onNavigateToHistoryDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = dimensionResource(R.dimen.spacing_md)

    val leftItems = recentSolves.filterIndexed { index, _ -> index % 2 == 0 }
    val rightItems = recentSolves.filterIndexed { index, _ -> index % 2 != 0 }

    Column(modifier = modifier) {
        // ── Header ─────────────────────────────────────────────────────
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
                color = CeladonPrimary,
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        Spacer(modifier = Modifier.height(spacing))

        // ── Two-column staggered ────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.Top   // ← Top alignment — columns grow downward independently
        ) {
            // Left column — items 0, 2
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                leftItems.forEach { item ->
                    RecentSolveCard(
                        item = item,
                        onClick = { onNavigateToHistoryDetail(item.id) }
                    )
                }
            }

            // Right column — items 1, 3
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                rightItems.forEach { item ->
                    RecentSolveCard(
                        item = item,
                        onClick = { onNavigateToHistoryDetail(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSolveCard(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = item.solveResult.category.toChipColor()

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_xl)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // ── Visual area ─────────────────────────────────────────────
            if (item.solveResult.imageUri != null) {
                AsyncImage(
                    model = item.solveResult.imageUri,
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
            } else {
                // Text query — emoji placeholder, shorter than image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = dimensionResource(R.dimen.radius_xl),
                                topEnd = dimensionResource(R.dimen.radius_xl)
                            )
                        )
                        .background(categoryColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.solveResult.category.emoji,
                        fontSize = 28.sp
                    )
                }
            }

            // ── Text content ────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.spacing_md))
            ) {
                CategoryChip(category = item.solveResult.category)

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))

                Text(
                    text = item.solveResult.whatThisIs.ifBlank {
                        item.solveResult.inputQuery ?: stringResource(R.string.history_unknown_item)
                    },
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
                TestFixtures.historyItemYesterday,
                TestFixtures.historyItemToday,
                TestFixtures.historyItemYesterday,
            ),
            onViewAll = {},
            onNavigateToHistoryDetail = {},
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_xl))
        )
    }
}