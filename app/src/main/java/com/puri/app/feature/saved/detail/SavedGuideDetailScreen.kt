package com.puri.app.feature.saved.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.analytics.ScreenNames
import com.puri.app.core.analytics.TrackScreen
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.mapper.displayDescription
import com.puri.app.core.ui.mapper.displayTitle
import com.puri.app.core.ui.mapper.toChipColor
import com.puri.app.core.ui.theme.CeladonPrimary
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.domain.model.GuideContent
import com.puri.app.domain.model.GuideSection
import com.puri.app.domain.model.GuideSectionType
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.model.SolveResult
import com.puri.app.feature.saved.components.ListSection
import com.puri.app.feature.saved.components.StepsSection
import com.puri.app.feature.saved.components.TableSection
import com.puri.app.feature.saved.components.TipSection
import com.puri.app.feature.saved.components.WarningSection
import com.puri.app.feature.solve.components.RecommendedActionCard
import com.puri.app.feature.solve.components.StepItem
import com.puri.app.feature.solve.components.TipCard
import com.puri.app.feature.solve.components.VisibleTextSection
import com.puri.app.feature.solve.components.WarningBlock
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedGuideDetailScreen(
    onBack: () -> Unit,
    viewModel: SavedGuideDetailViewModel = hiltViewModel()
) {
    TrackScreen(ScreenNames.SAVED_DETAIL)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { onBack() }

    Scaffold(
        topBar = {
            PuriTopBar(
                title = when (val state = uiState) {
                    is SavedGuideDetailUiState.Content ->
                        state.guide.displayTitle().take(28)

                    else -> stringResource(R.string.saved_detail_title)
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
            SavedGuideDetailUiState.Loading ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CeladonPrimary)
                }

            SavedGuideDetailUiState.Error ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.error_unknown))
                }

            is SavedGuideDetailUiState.Content ->
                SavedGuideDetailContent(
                    guide = state.guide,
                    content = state.content,
                    modifier = Modifier.padding(padding)
                )
        }
    }
}

@Composable
private fun SavedGuideDetailContent(
    guide: SavedGuide,
    content: GuideContent?,
    modifier: Modifier = Modifier
) {
    val categoryColor = guide.category.toChipColor()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Hero header ────────────────────────────────────────────────
        GuideHeroSection(
            guide = guide,
            categoryColor = categoryColor
        )

        Column(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.screen_horizontal_padding)
            )
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            when {
                // Pre-bundled — structured content
                guide.isPreBundled && content != null -> {
                    content.sections.forEachIndexed { index, section ->
                        var sectionVisible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(100L + index * 60L)
                            sectionVisible = true
                        }
                        AnimatedVisibility(
                            visible = sectionVisible,
                            enter = fadeIn() + slideInVertically { it / 5 }
                        ) {
                            GuideSectionBlock(section = section)
                        }
                        if (index < content.sections.lastIndex) {
                            Spacer(
                                modifier = Modifier.height(
                                    dimensionResource(R.dimen.spacing_xl)
                                )
                            )
                        }
                    }
                }

                // User-saved solve result
                guide.solveResult != null -> {
                    UserSolveResultContent(
                        solveResult = guide.solveResult,
                        isVisible = isVisible
                    )
                }

                !guide.description.isNullOrBlank() -> {
                    UserBasicContent(
                        description = guide.description,
                        isVisible = isVisible
                    )
                }


                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.guide_content_unavailable),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_bottom_nav)))
        }
    }
}

@Composable
private fun UserBasicContent(
    description: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        modifier = modifier
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GuideHeroSection(
    guide: SavedGuide,
    categoryColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        categoryColor.copy(alpha = 0.25f),
                        categoryColor.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Large emoji — decorative, faded
        Text(
            text = guide.category.emoji,
            fontSize = 140.sp,
            color = categoryColor.copy(alpha = 0.18f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 8.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    horizontal = dimensionResource(R.dimen.screen_horizontal_padding),
                    vertical = dimensionResource(R.dimen.spacing_xl)
                )
        ) {
            // Category pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(categoryColor.copy(alpha = 0.12f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        6.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(categoryColor)
                    )
                    Text(
                        text = guide.category.name.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = guide.displayTitle(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = guide.displayDescription(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Offline badge for pre-bundled
            if (guide.isPreBundled) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(CeladonPrimary.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = stringResource(R.string.available_offline_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = CeladonPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun UserSolveResultContent(
    solveResult: SolveResult,
    isVisible: Boolean
) {
    Column {
        AnimatedVisibility(visible = isVisible, enter = fadeIn()) {
            Column {
                if (solveResult.description.isNotBlank()) {
                    Text(
                        text = solveResult.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))
                }

                if (solveResult.visibleTexts.isNotEmpty()) {
                    VisibleTextSection(visibleTexts = solveResult.visibleTexts)
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))
                }

                solveResult.warning?.let { warning ->
                    WarningBlock(warning = warning)
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
                }
            }
        }

        if (solveResult.steps.isNotEmpty()) {
            solveResult.steps.forEachIndexed { index, step ->
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
                if (index < solveResult.steps.lastIndex) {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
                }
            }
        }

        solveResult.koreaTip?.let { tip ->
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
            TipCard(tip = tip)
        }

        solveResult.recommendedAction?.let { action ->
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
            RecommendedActionCard(action = action)
        }

    }
}

@Composable
private fun GuideSectionBlock(
    section: GuideSection,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (section.sectionType) {
            GuideSectionType.WARNING -> WarningSection(section = section)
            GuideSectionType.TIP -> TipSection(section = section)
            GuideSectionType.TABLE -> TableSection(section = section)
            GuideSectionType.STEPS -> StepsSection(section = section)
            GuideSectionType.LIST -> ListSection(section = section)
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@PreviewLightDark
@Composable
fun PreviewSavedGuideDetailScreen() {
    PuriTheme {
        SavedGuideDetailScreen(onBack = {})
    }
}