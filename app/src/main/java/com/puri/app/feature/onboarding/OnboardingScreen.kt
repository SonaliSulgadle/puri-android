package com.puri.app.feature.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.puri.app.R
import com.puri.app.core.analytics.LocalAnalytics
import com.puri.app.core.analytics.PuriEvent
import com.puri.app.core.analytics.ScreenNames
import com.puri.app.core.analytics.TrackScreen
import com.puri.app.core.ui.theme.GradientSnapEnd
import com.puri.app.core.ui.theme.GradientSnapStart
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.feature.onboarding.components.ImageToTextDemo
import com.puri.app.feature.onboarding.components.OfflineGuidesDemo
import com.puri.app.feature.onboarding.components.OnboardingDots
import com.puri.app.feature.onboarding.components.PuriBrandBadge
import com.puri.app.feature.onboarding.components.TextToTextDemo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    TrackScreen(ScreenNames.ONBOARDING)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.totalPages })
    val scope = rememberCoroutineScope()

    val analytics = LocalAnalytics.current
    LaunchedEffect(pagerState.currentPage) {
        analytics.log(PuriEvent.OnboardingPageViewed(pagerState.currentPage))
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collectLatest { page ->
                viewModel.onIntent(OnboardingIntent.SyncPage(page))
            }
    }

    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                OnboardingUiEffect.NavigateToHome -> onFinished()
            }
        }
    }

    val systemUiController = rememberSystemUiController()
    DisposableEffect(Unit) {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = false  // white icons on dark background
        )
        onDispose {
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = true  // dark icons for main app (light theme)
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            OnboardingPageContent(
                pageRes = pageResources[pageIndex],
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = dimensionResource(R.dimen.screen_horizontal_padding),
                    vertical = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page dots
            OnboardingDots(
                currentPage = pagerState.currentPage,
                totalPages = uiState.totalPages
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

            val isLastPage = pagerState.currentPage == uiState.totalPages - 1

            // Primary CTA button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(100.dp))
                    .background(
                        Brush.linearGradient(listOf(GradientSnapStart, GradientSnapEnd))
                    )
            ) {
                Button(
                    onClick = {
                        if (isLastPage) {
                            viewModel.onIntent(OnboardingIntent.Finish)
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    enabled = !uiState.isFinishing,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = if (isLastPage)
                            stringResource(R.string.onboarding_get_started)
                        else
                            stringResource(R.string.onboarding_next),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_xs))
                    )
                }
            }

            // Skip — only on non-last pages
            if (!isLastPage) {
                TextButton(
                    onClick = { viewModel.onIntent(OnboardingIntent.Skip) }
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_skip),
                        color = Color.White.copy(alpha = 0.45f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    pageRes: PageResource,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Brush.verticalGradient(pageRes.backgroundGradient))
    ) {
        Text(
            text = pageRes.bigEmoji,
            fontSize = 240.sp,
            color = Color.White.copy(alpha = 0.035f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = dimensionResource(R.dimen.screen_horizontal_padding),
                    vertical = 32.dp
                )
        ) {
            PuriBrandBadge(accentColor = pageRes.accentColor)

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(pageRes.headlineRes),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 42.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(pageRes.subtitleRes),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (pageRes.pageType) {
                PageType.IMAGE_TO_TEXT ->
                    ImageToTextDemo(accentColor = pageRes.accentColor)

                PageType.TEXT_TO_TEXT ->
                    TextToTextDemo(accentColor = pageRes.accentColor)

                PageType.OFFLINE_GUIDES ->
                    OfflineGuidesDemo(accentColor = pageRes.accentColor)
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF0D0B1E)
@Composable
private fun OnboardingScreenPreview() {
    PuriTheme {
        OnboardingScreen(onFinished = {})
    }
}