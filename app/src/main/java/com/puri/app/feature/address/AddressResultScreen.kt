package com.puri.app.feature.address

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puri.app.R
import com.puri.app.core.ui.components.PuriTopBar
import com.puri.app.core.ui.theme.IndigoPrimary
import com.puri.app.domain.model.AddressConfidence
import com.puri.app.domain.model.AddressType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressResultScreen(
    onBack: () -> Unit,
    viewModel: AddressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BackHandler { onBack() }

    // If result is null (e.g. back navigation), go back
    val result = uiState.result ?: run {
        onBack()
        return
    }

    Scaffold(
        topBar = {
            PuriTopBar(
                title = stringResource(R.string.address_result_title),
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
                .padding(horizontal = dimensionResource(R.dimen.screen_horizontal_padding))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            // Original input
            Text(
                text = stringResource(R.string.address_original_label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = result.original,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            // Converted address card
            Text(
                text = stringResource(R.string.address_converted_label).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = IndigoPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.radius_xl)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_xl))
                ) {
                    Text(
                        text = result.normalized,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (result.shortForm != result.normalized) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.shortForm,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ConfidencePill(confidence = result.confidence)
                        Spacer(modifier = Modifier.width(8.dp))
                        AddressTypePill(type = result.addressType)
                    }
                }
            }

            result.locationDetail?.let { detail ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_lg)))
                        .background(IndigoPrimary.copy(alpha = 0.08f))
                        .padding(dimensionResource(R.dimen.spacing_md)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))
                ) {
                    Text(
                        text = stringResource(R.string.address_pin_emoji),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.address_detail_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Warning note
            result.note?.let { note ->
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.radius_lg)))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(dimensionResource(R.dimen.spacing_md))
                ) {
                    Text(
                        text = "ℹ️  $note",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_2xl)))

            // Copy button — primary action
            Button(
                onClick = {
                    val clipboard = context.getSystemService(
                        Context.CLIPBOARD_SERVICE
                    ) as ClipboardManager
                    clipboard.setPrimaryClip(
                        ClipData.newPlainText("Korean address", result.normalized)
                    )
                    scope.launch {
                        snackbarHost.showSnackbar(
                            context.getString(R.string.address_copied)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.address_copy_button),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

            // Open in Naver Map (app) — secondary
            OutlinedButton(
                onClick = {
                    val naverIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = result.naverMapAppUrl.toUri()
                        setPackage("com.nhn.android.nmap")
                    }
                    if (naverIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(naverIntent)
                    } else {
                        // Naver Map not installed — open web
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, result.naverMapWebUrl.toUri())
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = null,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.address_open_naver),
                    color = IndigoPrimary
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))

            // Open in browser — tertiary
            OutlinedButton(
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, result.naverMapWebUrl.toUri())
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.OpenInBrowser,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.address_open_browser),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            // Disclaimer
            Text(
                text = stringResource(R.string.address_disclaimer),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_bottom_nav)))
        }
    }
}

@Composable
private fun ConfidencePill(confidence: AddressConfidence) {
    val (bgColor, textColor, label) = when (confidence) {
        AddressConfidence.HIGH ->
            Triple(Color(0xFF2E7D32).copy(alpha = 0.12f), Color(0xFF2E7D32), "High confidence")

        AddressConfidence.MEDIUM ->
            Triple(Color(0xFFE65100).copy(alpha = 0.12f), Color(0xFFE65100), "Verify recommended")

        AddressConfidence.LOW ->
            Triple(
                MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                MaterialTheme.colorScheme.error,
                "Verify before visiting"
            )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AddressTypePill(type: AddressType) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(IndigoPrimary.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = type.label,
            style = MaterialTheme.typography.labelSmall,
            color = IndigoPrimary
        )
    }
}