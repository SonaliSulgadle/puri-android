package com.puri.app.feature.solve.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
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
    onTextQueryChanged: (String) -> Unit,
    onSubmitQuery: () -> Unit,
    onViewAllHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textQuery by remember { mutableStateOf("") }

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
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            PuriTopBar()

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                query = textQuery,
                onQueryChange = {
                    textQuery = it
                    onTextQueryChanged(it)
                },
                onSubmit = onSubmitQuery
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

            DailySolvesCounter(remaining = state.dailySolvesRemaining)

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
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_bottom_nav)))
        }
    }
}

@Preview(showBackground = true)
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
            onTextQueryChanged = {},
            onSubmitQuery = {},
            onViewAllHistory = {}
        )
    }
}