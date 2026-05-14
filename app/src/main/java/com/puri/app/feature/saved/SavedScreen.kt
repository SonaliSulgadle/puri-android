package com.puri.app.feature.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.analytics.ScreenNames
import com.puri.app.core.analytics.TrackScreen
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.theme.CeladonPrimary
import com.puri.app.domain.model.SavedGuide
import com.puri.app.feature.saved.components.GuideCollectionBanner
import com.puri.app.feature.saved.components.GuideListItem
import com.puri.app.feature.saved.components.OfflineBanner
import com.puri.app.feature.saved.components.SavedEmptyState
import com.puri.app.feature.saved.components.SnapSolveCta
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onNavigateToSolve: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: SavedViewModel = hiltViewModel()
) {
    TrackScreen(ScreenNames.SAVED_GUIDES)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is SavedUiEffect.NavigateToSolve -> onNavigateToSolve()
                is SavedUiEffect.OpenGuideDetail -> onNavigateToDetail(effect.guideId)
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
        ) {
            when (val state = uiState) {
                SavedUiState.Loading -> SavedLoadingState()
                SavedUiState.Empty -> SavedEmptyState(
                    onNavigateToSolve = { viewModel.onIntent(SavedIntent.NavigateToSolve) }
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
    val hasUserSaves = state.userSavedGuides.isNotEmpty()

    // Tab index — 0 = Guides, 1 = My Saves
    // Only relevant when user has saves; otherwise no tabs shown
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {

        // Tabs — only appear when user has saved at least one result
        // Before first save: clean guides list with no tab overhead
        if (hasUserSaves) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = CeladonPrimary,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CeladonPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = stringResource(R.string.saved_title),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold
                            else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = stringResource(
                                R.string.saved_tab_my_saves,
                                state.userSavedGuides.size
                            ),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold
                            else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Content for selected tab
        when {
            // No tabs — show guides directly (first-time user experience)
            !hasUserSaves -> GuidesTab(
                state = state,
                onGuideClick = onGuideClick,
                onNavigateToSolve = onNavigateToSolve
            )

            selectedTab == 0 -> GuidesTab(
                state = state,
                onGuideClick = onGuideClick,
                onNavigateToSolve = onNavigateToSolve
            )

            selectedTab == 1 -> MySavesTab(
                guides = state.userSavedGuides,
                onGuideClick = onGuideClick,
                onNavigateToSolve = onNavigateToSolve
            )
        }
    }
}

@Composable
private fun GuidesTab(
    state: SavedUiState.Content,
    onGuideClick: (SavedGuide) -> Unit,
    onNavigateToSolve: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
        contentPadding = PaddingValues(
            start = dimensionResource(R.dimen.screen_horizontal_padding),
            end = dimensionResource(R.dimen.screen_horizontal_padding),
            bottom = dimensionResource(R.dimen.spacing_bottom_nav)
        ),
        modifier = modifier
    ) {
        item {
            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_lg)))
            Text(
                text = stringResource(R.string.saved_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_lg)))
        }

        if (state.isOfflineMode) {
            item(key = "offline_banner") {
                OfflineBanner(guideCount = state.preBundledGuides.size)
            }
        }

        item(key = "collection_banner") {
            GuideCollectionBanner()
        }

        if (state.preBundledGuides.isNotEmpty()) {
            item(key = "bundled_header") {
                Text(
                    text = stringResource(R.string.saved_essential_guides),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(state.preBundledGuides, key = { "row_${it.id}" }) { guide ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        dimensionResource(R.dimen.spacing_md)
                    )
                ) {
                    GuideListItem(guide = guide, onClick = { onGuideClick(guide) })
                }
            }
        }

        item(key = "snap_cta") {
            SnapSolveCta(onClick = onNavigateToSolve)
        }
    }
}

@Composable
private fun MySavesTab(
    guides: List<SavedGuide>,
    onGuideClick: (SavedGuide) -> Unit,
    onNavigateToSolve: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
        contentPadding = PaddingValues(
            start = dimensionResource(R.dimen.screen_horizontal_padding),
            end = dimensionResource(R.dimen.screen_horizontal_padding),
            bottom = dimensionResource(R.dimen.spacing_bottom_nav)
        ),
        modifier = modifier
    ) {
        item {
            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_lg)))
            Text(
                text = stringResource(R.string.saved_my_saves_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_lg)))
        }

        items(guides, key = { "saved_${it.id}" }) { guide ->
            GuideListItem(guide = guide, onClick = { onGuideClick(guide) })
        }

        // Prompt to solve more
        item(key = "solve_more_cta") {
            SnapSolveCta(onClick = onNavigateToSolve)
        }
    }
}

@Composable
private fun SavedLoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = CeladonPrimary)
    }
}