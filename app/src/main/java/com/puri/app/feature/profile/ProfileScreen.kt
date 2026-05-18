package com.puri.app.feature.profile

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.analytics.LocalAnalytics
import com.puri.app.core.analytics.PuriEvent
import com.puri.app.core.analytics.ScreenNames
import com.puri.app.core.analytics.TrackScreen
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.feature.profile.components.LocalDataCard
import com.puri.app.feature.profile.components.ProfileHeroCard
import com.puri.app.feature.profile.components.ProfileMenuCard
import com.puri.app.feature.profile.components.ProfileMenuDivider
import com.puri.app.feature.profile.components.ProfileMenuItem
import com.puri.app.feature.profile.components.ProfileSectionLabel
import com.puri.app.feature.profile.components.ProfileStatsRow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val FEEDBACK_FORM = "https://tally.so/r/VLvRxN"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToPrivacyPolicy: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val analytics = LocalAnalytics.current
    TrackScreen(ScreenNames.PROFILE)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val historyCount by viewModel.historyCount.collectAsStateWithLifecycle()
    val savedCount by viewModel.savedCount.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is ProfileUiEffect.ShowSnackbar ->
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(effect.messageRes))
                    }
            }
        }
    }

    // Confirmation dialog
    uiState.pendingClearType?.let { clearType ->
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(ProfileIntent.DismissDialog) },
            shape = RoundedCornerShape(dimensionResource(R.dimen.radius_xl)),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            title = {
                Text(
                    text = when (clearType) {
                        ClearType.HISTORY -> stringResource(R.string.profile_clear_history_title)
                        ClearType.SAVED_SOLVES -> stringResource(R.string.profile_clear_saved_title)
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = when (clearType) {
                        ClearType.HISTORY -> stringResource(R.string.profile_clear_history_body)
                        ClearType.SAVED_SOLVES -> stringResource(R.string.profile_clear_saved_body)
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onIntent(ProfileIntent.ConfirmClear) },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.action_clear))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.onIntent(ProfileIntent.DismissDialog) },
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            PuriTopBar(
                title = stringResource(R.string.profile_title),
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(R.dimen.screen_horizontal_padding),
                vertical = dimensionResource(R.dimen.spacing_lg)
            ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))
        ) {

            // ── Hero card ──────────────────────────────────────────────────
            item(key = "hero") {
                ProfileHeroCard(
                    appVersion = uiState.appVersion
                )
            }

            // ── Stats row ──────────────────────────────────────────────────
            item(key = "stats") {
                ProfileStatsRow(
                    todaySolves = uiState.todaySolves,
                    dailyLimit = uiState.dailyLimit,
                    totalSolves = uiState.totalSolves,
                    totalSaved = uiState.totalSaved
                )
            }

            // ── Data section ───────────────────────────────────────────────
            item(key = "data_header") {
                ProfileSectionLabel(text = stringResource(R.string.profile_section_data))
            }

            item(key = "data_card") {
                ProfileMenuCard {
                    ProfileMenuItem(
                        icon = Icons.Outlined.History,
                        label = stringResource(R.string.profile_clear_history),
                        tint = MaterialTheme.colorScheme.error,
                        onClick = {
                            if (historyCount > 0) {
                                viewModel.onIntent(ProfileIntent.ClearHistory)
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        context.getString(R.string.profile_nothing_to_clear)
                                    )
                                }
                            }

                        }
                    )
                    ProfileMenuDivider()
                    ProfileMenuItem(
                        icon = Icons.Outlined.Storage,
                        label = stringResource(R.string.profile_clear_saved_solves),
                        tint = MaterialTheme.colorScheme.error,
                        onClick = {
                            if (savedCount > 0) {
                                viewModel.onIntent(ProfileIntent.ClearSavedSolves)
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        context.getString(R.string.profile_nothing_to_clear)
                                    )
                                }
                            }

                        }
                    )
                }
            }

            // ── App section ────────────────────────────────────────────────
            item(key = "app_header") {
                ProfileSectionLabel(text = stringResource(R.string.profile_section_app))
            }

            item(key = "app_card") {
                ProfileMenuCard {
                    ProfileMenuItem(
                        icon = Icons.Outlined.Star,
                        label = stringResource(R.string.profile_rate_app),
                        onClick = {
                            try {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW).apply {
                                        data = "market://details?id=${context.packageName}".toUri()
                                        setPackage("com.android.vending")
                                    }
                                )
                            } catch (e: Exception) {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
                                    )
                                )
                            }
                        }
                    )
                    ProfileMenuDivider()
                    ProfileMenuItem(
                        icon = Icons.Outlined.PrivacyTip,
                        label = stringResource(R.string.profile_privacy_policy),
                        onClick = {
                            onNavigateToPrivacyPolicy()
                            analytics.log(PuriEvent.ProfilePrivacyTapped)
                        }
                    )
                    ProfileMenuDivider()
                    ProfileMenuItem(
                        icon = Icons.Outlined.Feedback,
                        label = stringResource(R.string.profile_feedback),
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                FEEDBACK_FORM.toUri()
                            )
                            context.startActivity(intent)
                            analytics.log(PuriEvent.FeedbackOpened)
                        }
                    )
                    ProfileMenuDivider()
                    ProfileMenuItem(
                        icon = Icons.Outlined.Info,
                        label = stringResource(R.string.profile_version),
                        value = uiState.appVersion,
                        onClick = null
                    )
                }
            }

            // ── Local data statement ───────────────────────────────────────
            item(key = "local_data") {
                LocalDataCard()
            }

            // Bottom nav padding
            item(key = "bottom_space") {
                Spacer(
                    modifier = Modifier
                        .height(dimensionResource(R.dimen.spacing_bottom_nav))
                        .navigationBarsPadding()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    PuriTheme {
        ProfileScreen(
            onNavigateToPrivacyPolicy = {}
        )
    }
}