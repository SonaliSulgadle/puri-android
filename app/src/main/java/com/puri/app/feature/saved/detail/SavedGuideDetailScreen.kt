package com.puri.app.feature.saved.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.puri.app.feature.saved.components.ListSection
import com.puri.app.feature.saved.components.StepsSection
import com.puri.app.feature.saved.components.TableSection
import com.puri.app.feature.saved.components.TipSection
import com.puri.app.feature.saved.components.WarningSection
import com.puri.app.feature.solve.components.VisibleTextSection

@Composable
fun SavedGuideDetailScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: SavedGuideDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler { onBack() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        PuriResponseTopBar(title = stringResource(R.string.saved_detail_title), onBack = onBack)
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
            VisibleTextSection(
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