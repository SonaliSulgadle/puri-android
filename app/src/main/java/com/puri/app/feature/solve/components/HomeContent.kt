package com.puri.app.feature.solve.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import com.puri.app.R
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.feature.solve.SolveUiState
import com.puri.app.util.TestFixtures

@Composable
fun HomeContent(
    state: SolveUiState.Idle,
    snackbarHostState: SnackbarHostState,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onSubmitQuery: (String) -> Unit,
    onViewAllHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding))
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            PuriTopBar()

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                initialQuery = state.currentQuery,
                onSubmit = {
                    onSubmitQuery(it)
                }
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

            DailySolvesCounter(remaining = state.dailySolvesRemaining, state.dailySolvesLimit)

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

            SnapAndSolveCard(
                onCameraClick = onOpenCamera,
                onGalleryClick = onOpenGallery
            )

            if (state.recentSolves.isNotEmpty()) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))
                RecentSolvesSection(
                    recentSolves = state.recentSolves,
                    onViewAll = onViewAllHistory
                )
            } else {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))
                FirstTimeHint()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_bottom_nav)))
        }
    }
}

@Composable
private fun FirstTimeHint() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.upward_point_emoji),
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        Text(
            text = stringResource(R.string.home_first_time_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeContentPreview() {
    PuriTheme {
        HomeContent(
            state = SolveUiState.Idle(
                dailySolvesRemaining = 8,
                recentSolves = listOf(
                    TestFixtures.historyItemToday,
                    TestFixtures.historyItemYesterday
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onOpenCamera = {},
            onOpenGallery = {},
            onSubmitQuery = {},
            onViewAllHistory = {}
        )
    }
}