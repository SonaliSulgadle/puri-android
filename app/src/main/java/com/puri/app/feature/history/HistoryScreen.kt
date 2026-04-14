package com.puri.app.feature.history

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.core.util.DateTimeUtils
import com.puri.app.domain.model.HistoryItem
import com.puri.app.feature.solve.components.CategoryChip
import com.puri.app.util.TestFixtures
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            PuriTopBar()

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            Text(
                text = stringResource(R.string.history_title),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = stringResource(R.string.history_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            when (val state = uiState) {
                HistoryUiState.Loading -> HistoryLoadingState()
                HistoryUiState.Empty -> HistoryEmptyState()
                is HistoryUiState.Error -> HistoryErrorState()
                is HistoryUiState.Content -> HistoryContent(
                    state = state,
                    onSearchChanged = {
                        viewModel.onIntent(HistoryIntent.SearchQueryChanged(it))
                    },
                    onDelete = { viewModel.onIntent(HistoryIntent.DeleteItem(it)) },
                    onItemClick = { viewModel.onIntent(HistoryIntent.ViewItem(it)) }
                )
            }
        }
    }
}

@Composable
private fun HistoryContent(
    state: HistoryUiState.Content,
    onSearchChanged: (String) -> Unit,
    onDelete: (Long) -> Unit,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChanged,
            placeholder = {
                Text(
                    text = stringResource(R.string.history_search_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill)),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
            contentPadding = PaddingValues(
                bottom = dimensionResource(R.dimen.spacing_bottom_nav)
            )
        ) {
            state.filteredItems.forEach { (dateLabel, items) ->
                item(key = "header_$dateLabel") {
                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(
                            vertical = dimensionResource(R.dimen.spacing_sm)
                        )
                    )
                }
                items(
                    items = items,
                    key = { "item_${it.id}" }
                ) { item ->
                    SwipeToDeleteHistoryItem(
                        item = item,
                        onDelete = { onDelete(item.id) },
                        onItemClick = { onItemClick(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SwipeToDeleteHistoryItem(
    item: HistoryItem,
    onDelete: () -> Unit,
    onItemClick: () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val deleteThreshold = -200f

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "swipe_offset_${item.id}"
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        // Delete icon revealed on swipe
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .padding(end = dimensionResource(R.dimen.spacing_lg)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = stringResource(R.string.cd_delete),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_lg))
            )
        }

        // Swipeable card
        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .pointerInput(item.id) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < deleteThreshold) {
                                onDelete()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            offsetX = 0f
                        }
                    ) { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceAtMost(0f)
                    }
                }
        ) {
            HistoryItemCard(
                item = item,
                onClick = onItemClick
            )
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_lg)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category emoji avatar
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.solveResult.category.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                CategoryChip(category = item.solveResult.category)

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))

                Text(
                    text = item.solveResult.whatThisIs.ifBlank {
                        stringResource(R.string.history_unknown_item)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))

                Text(
                    text = DateTimeUtils.formatRelativeTimestamp(item.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = stringResource(R.string.history_view_solve),
                style = MaterialTheme.typography.labelMedium,
                color = IndigoPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HistoryEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.History,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
        Text(
            text = stringResource(R.string.history_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        Text(
            text = stringResource(R.string.history_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HistoryLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = IndigoPrimary)
    }
}

@Composable
private fun HistoryErrorState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.error_unknown),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@PreviewLightDark
@Composable
private fun HistoryEmptyStatePreview() {
    PuriTheme { HistoryEmptyState() }
}

@PreviewLightDark
@Composable
private fun HistoryItemCardPreview() {
    PuriTheme {
        HistoryItemCard(
            item = TestFixtures.historyItemToday,
            onClick = {}
        )
    }
}