package com.puri.app.feature.solve.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.puri.app.R
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.theme.CeladonPrimary
import com.puri.app.core.ui.theme.CeramicWhite
import com.puri.app.core.ui.theme.GradientSnapEnd
import com.puri.app.core.ui.theme.GradientSnapStart
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.domain.model.SolveResult
import com.puri.app.util.TestFixtures
import kotlinx.coroutines.delay

@Composable
fun ResponseCard(
    result: SolveResult,
    isSaved: Boolean,
    onSave: () -> Unit,
    onSolveAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    Scaffold(
        topBar = {
            PuriTopBar(
                title = result.whatThisIs.take(28),
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Image preview ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.image_preview_height))
                    .clip(
                        RoundedCornerShape(
                            bottomStart = dimensionResource(R.dimen.radius_xl),
                            bottomEnd = dimensionResource(R.dimen.radius_xl)
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                if (result.imageUri != null) {
                    AsyncImage(
                        model = result.imageUri,
                        contentDescription = stringResource(R.string.cd_solve_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    // Text query — show category icon as placeholder
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.ImageNotSupported,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = result.category.emoji,
                                fontSize = 32.sp
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding))
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 4 }
                ) {
                    Text(
                        text = stringResource(R.string.response_analysis_complete).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = CeladonPrimary
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(300, delayMillis = 100)) +
                            slideInVertically(tween(300, delayMillis = 100)) { it / 4 }
                ) {
                    Text(
                        text = result.whatThisIs,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                CategoryChip(category = result.category)

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

                SectionHeader(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.response_what_this_is)
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                Text(
                    text = result.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (result.visibleTexts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))
                    VisibleTextSection(visibleTexts = result.visibleTexts)
                }

                result.warning?.let { warning ->
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
                    WarningBlock(warning = warning)
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

                if (result.steps.isNotEmpty()) {

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(300, delayMillis = 200)) +
                                slideInVertically(tween(300, delayMillis = 200)) { it / 4 }
                    ) {
                        SectionHeader(
                            icon = Icons.Outlined.ArrowDownward,
                            title = stringResource(R.string.response_what_to_do)
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                    result.steps.forEachIndexed { index, step ->
                        var stepVisible by remember { mutableStateOf(false) }

                        LaunchedEffect(key1 = step.order) {
                            delay(300L + (index * 100L))
                            stepVisible = true
                        }

                        AnimatedVisibility(
                            visible = stepVisible,
                            enter = fadeIn(tween(250)) + slideInHorizontally(tween(250)) { -it / 3 }
                        ) {
                            StepItem(step = step)
                        }

                        if (index < result.steps.lastIndex) {
                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
                        }
                    }
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
                }
                result.recommendedAction?.let { action ->
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
                    RecommendedActionCard(action = action)
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
                }

                Text(
                    text = stringResource(R.string.response_verify_disclaimer),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.spacing_xl))
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                result.koreaTip?.let { tip ->
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
                    TipCard(tip = tip)
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

                // ── Solve it for me ────────────────────────────────────────
                Button(
                    onClick = onSolveAgain,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(listOf(GradientSnapStart, GradientSnapEnd)),
                                RoundedCornerShape(dimensionResource(R.dimen.radius_pill))
                            )
                            .padding(dimensionResource(R.dimen.spacing_lg)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.response_solve_again),
                            color = CeramicWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

                // ── Save and Share ─────────────────────────────────────────
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onSave,
                        enabled = !isSaved,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill))
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark
                            else Icons.Outlined.BookmarkBorder,
                            contentDescription = stringResource(R.string.cd_save_guide),
                            tint = CeladonPrimary
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))
                        Text(
                            text = if (isSaved) stringResource(R.string.response_saved)
                            else stringResource(R.string.response_save)
                        )
                    }

                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))

                    OutlinedButton(
                        onClick = { /* share intent */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill))
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = stringResource(R.string.cd_share_result)
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))
                        Text(stringResource(R.string.response_share))
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_bottom_nav)))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ResponseCardPreview() {
    PuriTheme {
        ResponseCard(
            result = TestFixtures.trashSolveResult,
            isSaved = false,
            onSave = {},
            onSolveAgain = {},
            onBack = {}
        )
    }
}