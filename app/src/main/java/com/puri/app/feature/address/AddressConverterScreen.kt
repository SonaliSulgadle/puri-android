package com.puri.app.feature.address

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.theme.GradientSnapEnd
import com.puri.app.core.ui.theme.GradientSnapStart
import com.puri.app.core.ui.theme.IndigoPrimary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressConverterScreen(
    onBack: () -> Unit,
    onResultReady: () -> Unit,
    viewModel: AddressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is AddressUiEffect.NavigateToResult ->
                    onResultReady()

                is AddressUiEffect.ShowSnackbar ->
                    scope.launch {
                        snackbarHost.showSnackbar(context.getString(effect.messageRes))
                    }
            }
        }
    }

    Scaffold(
        topBar = {
            PuriTopBar(
                title = stringResource(R.string.address_title),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = IndigoPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
        modifier = Modifier
            .statusBarsPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Hero header ────────────────────────────────────────────
            AddressHeroHeader()

            Column(
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding))
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

                // ── Input area ─────────────────────────────────────────
                Text(
                    text = stringResource(R.string.address_input_label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

                // Multi-line input with clear button at TOP right
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = uiState.input,
                        onValueChange = { viewModel.onIntent(AddressIntent.UpdateInput(it)) },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.address_input_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.onIntent(AddressIntent.Convert)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(R.dimen.address_input_height)),
                        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_xl)),
                        maxLines = 6,
                        enabled = !uiState.isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    // Clear button — positioned at top-end, not center
                    if (uiState.input.isNotBlank() && !uiState.isLoading) {
                        IconButton(
                            onClick = { viewModel.onIntent(AddressIntent.UpdateInput("")) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(
                                    top = dimensionResource(R.dimen.spacing_xs),
                                    end = dimensionResource(R.dimen.spacing_xs)
                                )
                                .size(dimensionResource(R.dimen.icon_button_sm))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.cd_clear),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_sm))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

                // Supported formats hint
                Text(
                    text = stringResource(R.string.address_examples),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

                Text(
                    text = pluralStringResource(
                        id = R.plurals.address_converts_remaining,
                        count = uiState.convertsRemaining,
                        uiState.convertsRemaining
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        uiState.convertsRemaining <= 1 ->
                            MaterialTheme.colorScheme.error

                        uiState.convertsRemaining <= 2 ->
                            Color(0xFFE65100)

                        else ->
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                    }
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

                // ── Convert button ─────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            if (uiState.input.isNotBlank() && !uiState.isLoading)
                                Brush.linearGradient(listOf(GradientSnapStart, GradientSnapEnd))
                            else
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceContainerHigh,
                                        MaterialTheme.colorScheme.surfaceContainerHigh
                                    )
                                )
                        )
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.onIntent(AddressIntent.Convert)
                        },
                        enabled = uiState.input.isNotBlank() && !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))
                            Text(
                                text = stringResource(R.string.address_converting),
                                color = Color.White,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.address_convert_button),
                                color = if (uiState.input.isNotBlank()) Color.White
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_xs))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_bottom_nav)))
            }
        }
    }
}

@Composable
private fun AddressHeroHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D1117),
                        Color(0xFF161B2E),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(
                horizontal = dimensionResource(R.dimen.screen_horizontal_padding),
                vertical = dimensionResource(R.dimen.spacing_2xl)
            )
    ) {
        // Decorative emoji
        Text(
            text = stringResource(R.string.address_pin_emoji),
            fontSize = 100.sp,
            modifier = Modifier.align(Alignment.TopEnd),
            color = Color.White.copy(alpha = 0.06f)
        )

        Column {
            // Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(IndigoPrimary.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.address_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9EA3FF),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

            Text(
                text = stringResource(R.string.address_headline),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.address_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}