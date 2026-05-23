package com.puri.app.feature.history.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.puri.app.R
import com.puri.app.core.analytics.ScreenNames
import com.puri.app.core.analytics.TrackScreen
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.mapper.toChipColor
import com.puri.app.core.ui.theme.CeladonPrimary
import com.puri.app.core.ui.theme.CeramicWhite
import com.puri.app.core.ui.util.StatusBarIconColor
import com.puri.app.core.util.DateTimeUtils
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.model.SolveResult
import com.puri.app.feature.solve.components.RecommendedActionCard
import com.puri.app.feature.solve.components.SectionHeader
import com.puri.app.feature.solve.components.StepItem
import com.puri.app.feature.solve.components.TipCard
import com.puri.app.feature.solve.components.VisibleTextSection
import com.puri.app.feature.solve.components.WarningBlock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    onBack: () -> Unit,
    viewModel: HistoryDetailViewModel = hiltViewModel()
) {
    TrackScreen(ScreenNames.HISTORY_DETAIL)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { onBack() }

    val isDark = isSystemInDarkTheme()
    StatusBarIconColor(darkIcons = !isDark)

    Scaffold(
        topBar = {
            PuriTopBar(
                title = when (val state = uiState) {
                    is HistoryDetailUiState.Content ->
                        state.historyItem.solveResult.whatThisIs.take(28)

                    else -> stringResource(R.string.history_detail_title)
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = CeladonPrimary
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (val state = uiState) {
            HistoryDetailUiState.Loading ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CeladonPrimary)
                }

            HistoryDetailUiState.Error ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.error_unknown))
                }

            is HistoryDetailUiState.Content ->
                HistoryDetailContent(
                    historyItem = state.historyItem,
                    isSaved = state.isSaved,
                    onSave = { viewModel.saveResult() },
                    modifier = Modifier.padding(padding)
                )
        }
    }
}

@Composable
private fun HistoryDetailContent(
    historyItem: HistoryItem,
    isSaved: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = historyItem.solveResult
    val categoryColor = result.category.toChipColor()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Hero image or category header ──────────────────────────────
        HeroSection(result = result, categoryColor = categoryColor)

        Column(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.screen_horizontal_padding)
            )
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            // ── Metadata row ───────────────────────────────────────────
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { it / 4 }
            ) {
                MetadataRow(historyItem = historyItem, categoryColor = categoryColor)
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            // ── Description ────────────────────────────────────────────
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically { it / 4 }
            ) {
                Text(
                    text = result.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Visible text ───────────────────────────────────────────
            if (result.visibleTexts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))
                AnimatedVisibility(visible = isVisible, enter = fadeIn()) {
                    VisibleTextSection(visibleTexts = result.visibleTexts)
                }
            }

            // ── Warning ────────────────────────────────────────────────
            result.warning?.let { warning ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
                WarningBlock(warning = warning)
            }

            // ── Steps ──────────────────────────────────────────────────
            if (result.steps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))
                SectionHeader(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.response_what_to_do)
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                result.steps.forEachIndexed { index, step ->
                    var stepVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(200L + index * 80L)
                        stepVisible = true
                    }
                    AnimatedVisibility(
                        visible = stepVisible,
                        enter = fadeIn() + slideInVertically { it / 3 }
                    ) {
                        StepItem(step = step)
                    }
                    if (index < result.steps.lastIndex) {
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
                    }
                }
            }

            // ── Tip ────────────────────────────────────────────────────
            result.koreaTip?.let { tip ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
                TipCard(tip = tip)
            }

            // ── Recommended action ─────────────────────────────────────
            result.recommendedAction?.let { action ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
                RecommendedActionCard(action = action)
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            // ── Save button ────────────────────────────────────────────
            OutlinedButton(
                onClick = onSave,
                enabled = !isSaved,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Bookmark
                    else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = CeladonPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSaved) stringResource(R.string.response_saved)
                    else stringResource(R.string.response_save),
                    color = CeladonPrimary
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_bottom_nav)))
        }
    }
}

@Composable
private fun HeroSection(
    result: SolveResult,
    categoryColor: Color
) {
    if (result.imageUri != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            AsyncImage(
                model = result.imageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient overlay at bottom for text legibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        }
    } else {
        // Text query — colored category header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            categoryColor.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = result.category.emoji, fontSize = 56.sp)
                Spacer(modifier = Modifier.height(8.dp))
                result.inputQuery?.let { query ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(CeramicWhite.copy(alpha = 0.1f))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "\"$query\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataRow(
    historyItem: HistoryItem,
    categoryColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Category badge
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(categoryColor.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = historyItem.solveResult.category.name,
                style = MaterialTheme.typography.labelSmall,
                color = categoryColor,
                fontWeight = FontWeight.Bold
            )
        }

        // Timestamp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = DateTimeUtils.formatRelativeTimestamp(historyItem.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}