package com.puri.app.feature.saved.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.ui.components.PuriResponseTopBar
import com.puri.app.core.ui.mapper.displayDescription
import com.puri.app.core.ui.mapper.displayTitle
import com.puri.app.core.ui.theme.GradientHeroEnd
import com.puri.app.core.ui.theme.GradientHeroStart
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.domain.model.GuideContent
import com.puri.app.domain.model.GuideSection
import com.puri.app.domain.model.GuideSectionType
import com.puri.app.domain.model.SavedGuide

@Composable
fun SavedGuideDetailScreen(
    onBack: () -> Unit,
    viewModel: SavedGuideDetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        PuriResponseTopBar(onBack = onBack)
        when (val state = uiState) {
            SavedGuideDetailUiState.Loading ->
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = IndigoPrimary)
                }

            SavedGuideDetailUiState.Error ->
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(stringResource(R.string.error_unknown))
                }

            is SavedGuideDetailUiState.Content ->
                SavedGuideDetailContent(
                    guide = state.guide,
                    content = state.content,
                    onBack = onBack
                )
        }

    }
}

@Composable
fun SavedGuideDetailContent(guide: SavedGuide, content: GuideContent?, onBack: () -> Unit) {
    // Hero header
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = dimensionResource(R.dimen.radius_xl),
                    bottomEnd = dimensionResource(R.dimen.radius_xl)
                )
            )
            .background(
                Brush.linearGradient(listOf(GradientHeroStart, GradientHeroEnd))
            )
            .padding(dimensionResource(R.dimen.spacing_2xl))
    ) {
        Text(
            text = guide.category.emoji,
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        Text(
            text = guide.displayTitle(),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
        Text(
            text = guide.displayDescription(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding))
    ) {
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

        // For pre-bundled guides — render guide content
        if (guide.isPreBundled && guide.guideKey != null) {
            content?.sections?.forEach { section ->
                GuideSectionBlock(section = section)
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))
            }
        }

        // For user-saved solves — render solve result
        if (guide.solveResult != null) {
            com.puri.app.feature.solve.components.VisibleTextSection(
                visibleTexts = guide.solveResult.visibleTexts
            )
            if (guide.solveResult.visibleTexts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))
            }

            if (guide.solveResult.steps.isNotEmpty()) {
                guide.solveResult.steps.forEach { step ->
                    com.puri.app.feature.solve.components.StepItem(step = step)
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
                }
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_bottom_nav)))
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
private fun TableSection(section: GuideSection) {
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

@Composable
private fun StepsSection(section: GuideSection) {
    Column {
        SectionTitle(text = section.title)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        section.items.forEachIndexed { index, item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.spacing_md)
                ),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(IndigoPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = item.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp)
                )
            }
            if (index < section.items.lastIndex) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
            }
        }
    }
}

@Composable
private fun ListSection(section: GuideSection) {
    Column {
        SectionTitle(text = section.title)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        section.items.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyMedium,
                    color = IndigoPrimary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = item.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
        }
    }
}

@Composable
private fun WarningSection(section: GuideSection) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_lg)),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_md))
            )
            Column {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
                section.items.forEach { item ->
                    if (item.detail.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.detail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    if (item.korean.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.korean,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.translation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TipSection(section: GuideSection) {
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_lg)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_lg)),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_md))
            )
            Column {
                Text(
                    text = "Tip",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.Bold
                )
                section.items.forEach { item ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
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
        SavedGuideDetailScreen({})
    }
}