package com.puri.app.feature.solve.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.puri.app.R
import com.puri.app.core.ui.theme.GradientSnapEnd
import com.puri.app.core.ui.theme.GradientSnapStart
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.core.ui.theme.PuriTheme
import com.puri.app.domain.model.SolveResult
import com.puri.app.util.TestFixtures

@Composable
fun ResponseCard(
    result: SolveResult,
    isSaved: Boolean,
    onSave: () -> Unit,
    onSolveAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Image preview ──────────────────────────────────────────────
        result.imageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = stringResource(R.string.cd_solve_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.image_preview_height))
                    .clip(
                        RoundedCornerShape(
                            bottomStart = dimensionResource(R.dimen.radius_xl),
                            bottomEnd = dimensionResource(R.dimen.radius_xl)
                        )
                    )
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

            Text(
                text = stringResource(R.string.response_analysis_complete).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = IndigoPrimary
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))

            Text(
                text = result.whatThisIs,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))

            Text(
                text = result.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                style = MaterialTheme.typography.bodyLarge
            )

            result.warning?.let { warning ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))
                WarningBlock(warning = warning)
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            SectionHeader(
                icon = Icons.Outlined.Info,
                title = stringResource(R.string.response_what_to_do)
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

            result.steps.forEach { step ->
                StepItem(step = step)
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
            }

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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(GradientSnapStart, GradientSnapEnd)),
                            RoundedCornerShape(dimensionResource(R.dimen.radius_pill))
                        )
                        .padding(dimensionResource(R.dimen.spacing_lg)),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.response_solve_it_for_me),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

            // ── Save and Share ─────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill))
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark
                        else Icons.Outlined.BookmarkBorder,
                        contentDescription = stringResource(R.string.cd_save_guide),
                        tint = IndigoPrimary
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))
                    Text(stringResource(R.string.response_save))
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

@Preview(showBackground = true)
@Composable
private fun ResponseCardPreview() {
    PuriTheme {
        ResponseCard(
            result = TestFixtures.trashSolveResult,
            isSaved = false,
            onSave = {},
            onSolveAgain = {}
        )
    }
}