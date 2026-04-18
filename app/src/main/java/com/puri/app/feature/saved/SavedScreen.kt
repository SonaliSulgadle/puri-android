package com.puri.app.feature.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.domain.model.SavedGuide
import com.puri.app.feature.saved.components.FeaturedGuideCard
import com.puri.app.feature.saved.components.GuideListItem
import com.puri.app.feature.saved.components.OfflineBanner
import com.puri.app.feature.saved.components.SavedEmptyState
import com.puri.app.feature.saved.components.SnapSolveCta
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onNavigateToSolve: () -> Unit,
    viewModel: SavedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is SavedUiEffect.NavigateToSolve -> onNavigateToSolve()
                is SavedUiEffect.OpenGuideDetail -> {
                    // navigate to guide detail screen
                }
            }
        }
    }

    Scaffold(
        topBar = {
            PuriTopBar(
                title = stringResource(R.string.saved_title),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding))
        ) {
            when (val state = uiState) {
                SavedUiState.Loading -> SavedLoadingState()
                SavedUiState.Empty -> SavedEmptyState(
                    onNavigateToSolve = {
                        viewModel.onIntent(SavedIntent.NavigateToSolve)
                    }
                )

                is SavedUiState.Content -> SavedContent(
                    state = state,
                    onGuideClick = { viewModel.onIntent(SavedIntent.OpenGuide(it)) },
                    onNavigateToSolve = { viewModel.onIntent(SavedIntent.NavigateToSolve) }
                )
            }
        }
    }
}

@Composable
private fun SavedContent(
    state: SavedUiState.Content,
    onGuideClick: (SavedGuide) -> Unit,
    onNavigateToSolve: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
        contentPadding = PaddingValues(
            bottom = dimensionResource(R.dimen.spacing_bottom_nav)
        ),
        modifier = modifier
    ) {
        item {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
            Text(
                text = stringResource(R.string.saved_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

        }
        // Offline banner
        if (state.isOfflineMode) {
            item(key = "offline_banner") {
                OfflineBanner(guideCount = state.preBundledGuides.size)
            }
        }

        // Featured guide — large card
        state.featuredGuide?.let { guide ->
            item(key = "featured_${guide.id}") {
                FeaturedGuideCard(
                    guide = guide,
                    onClick = { onGuideClick(guide) }
                )
            }
        }

        // Pre-bundled guides section
        if (state.preBundledGuides.isNotEmpty()) {
            item(key = "bundled_header") {
                Text(
                    text = stringResource(R.string.saved_essential_guides),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(
                items = state.preBundledGuides.filter { !it.isFeatured },
                key = { "bundled_${it.id}" }
            ) { guide ->
                GuideListItem(guide = guide, onClick = { onGuideClick(guide) })
            }
        }

        // User saved guides section
        if (state.userSavedGuides.isNotEmpty()) {
            item(key = "saved_header") {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
                Text(
                    text = stringResource(R.string.saved_my_saves),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(
                items = state.userSavedGuides,
                key = { "saved_${it.id}" }
            ) { guide ->
                GuideListItem(guide = guide, onClick = { onGuideClick(guide) })
            }
        }

        // Snap & Solve CTA at bottom
        item(key = "snap_cta") {
            SnapSolveCta(onClick = onNavigateToSolve)
        }
    }
}

@Composable
private fun SavedLoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = IndigoPrimary)
    }
}