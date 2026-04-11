package com.puri.app.feature.solve.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.puri.app.R
import com.puri.app.core.ui.theme.PuriTheme

@Composable
fun SearchBar(
    initialQuery: String,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf(initialQuery) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        placeholder = {
            Text(
                text = stringResource(R.string.home_search_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = if (query.isNotBlank()) {
            {
                IconButton(onClick = { query = "" }) {
                    Icon(Icons.Outlined.Clear, contentDescription = "Clear")
                }
            }
        } else null,
        shape = RoundedCornerShape(dimensionResource(R.dimen.radius_pill)),
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSubmit(query)
                focusManager.clearFocus()
            }
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {
    PuriTheme { SearchBar(initialQuery = "", onSubmit = {}) }
}