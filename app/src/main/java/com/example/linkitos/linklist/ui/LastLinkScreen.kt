package com.example.linkitos.linklist.ui

import android.webkit.URLUtil
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.linkitos.ErrorDialog
import com.example.linkitos.LoadingWheel
import com.example.linkitos.R
import com.example.linkitos.api.ApiResponseStatus
import com.example.linkitos.models.Link

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LastLinkScreen(
    onLinkClicked: (link: Link) -> Unit,
    onGoToListButtonClicked: () -> Unit,
    linkListViewModel: LinkListViewModel,
) {
    val state = linkListViewModel.state.collectAsStateWithLifecycle().value
    val status = state.status
    val link = remember { mutableStateOf("") }
    val showInvalidUrlDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LinkListToolbar()
        }
    ) {
        Box {
            Content(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                linkToSend = link.value,
                onLinkToSendChanged = {
                    link.value = it
                },
                latestLink = state.latestLink,
                onSendLinkButtonClick = {
                    val url = link.value
                    if (URLUtil.isValidUrl(url)) {
                        linkListViewModel.shortenLink(url)
                    } else {
                        showInvalidUrlDialog.value = true
                    }
                },
                onLinkClicked = onLinkClicked,
                onGoToListButtonClicked = onGoToListButtonClicked,
            )

            when (status) {
                is ApiResponseStatus.Loading -> {
                    LoadingWheel()
                }

                is ApiResponseStatus.Error -> {
                    ErrorDialog(
                        message = status.message
                    ) {
                        linkListViewModel.resetApiResponseStatus()
                    }
                }

                is ApiResponseStatus.Success -> {
                    link.value = ""
                    linkListViewModel.resetApiResponseStatus()
                }

                else -> {
                    // Status is None, do nothing
                }
            }

            if (showInvalidUrlDialog.value) {
                ErrorDialog(message = stringResource(id = R.string.invalid_link, link.value)) {
                    showInvalidUrlDialog.value = false
                }
            }
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    linkToSend: String,
    onLinkToSendChanged: (newLink: String) -> Unit,
    latestLink: Link?,
    onSendLinkButtonClick: () -> Unit,
    onLinkClicked: (link: Link) -> Unit,
    onGoToListButtonClicked: () -> Unit,
) {

    Column(
        modifier = modifier,
    ) {
        AddLinkRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_regular)),
            link = linkToSend,
            onLinkFieldChanged = {
                onLinkToSendChanged(it)
            },
            onSendLinkButtonClick = {
                onSendLinkButtonClick()
            },
        )

        Text(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.padding_regular),
                    end = dimensionResource(id = R.dimen.padding_regular),
                    top = dimensionResource(id = R.dimen.padding_medium)
                ),
            text = stringResource(id = R.string.most_recent_link),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Text(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.padding_regular),
                    end = dimensionResource(id = R.dimen.padding_regular),
                    top = dimensionResource(id = R.dimen.padding_small),
                    bottom = dimensionResource(id = R.dimen.padding_medium)
                ),
            text = stringResource(id = R.string.click_to_open_long_click_to_copy),
            fontSize = 12.sp
        )

        if (latestLink == null) {
            Box(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.link_list_empty_view_spacing),
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.most_recent_link_empty_text),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LastLink(
                latestLink,
                onLinkClicked,
            )
        }

        Button(onClick = {
            onGoToListButtonClicked()
        }) {
            Text(stringResource(id = R.string.recently_shortened_links))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LastLink(
    link: Link,
    onLinkClicked: (link: Link) -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_regular))
            .semantics { testTag = "link-${link.short}" }
            .combinedClickable(
                onClick = {
                    onLinkClicked(link)
                },
                onLongClick = {
                    clipboardManager.setText(AnnotatedString((link.short)))
                },
            )
    ) {
        Text(
            text = stringResource(id = R.string.link_alias, link.alias),
            fontSize = 14.sp
        )
        Text(
            text = link.short,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLinkRow(
    modifier: Modifier,
    link: String,
    onLinkFieldChanged: (link: String) -> Unit,
    onSendLinkButtonClick: () -> Unit,
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .semantics { testTag = "link-field" },
            value = link,
            onValueChange = onLinkFieldChanged,
            placeholder = {
                Text(text = stringResource(id = R.string.link_to_shorten))
            }
        )

        IconButton(
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.padding_regular))
                .semantics { testTag = "send-link-button" },
            onClick = {
                onSendLinkButtonClick()
            },
            enabled = link.isNotEmpty()
        ) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Sharp.Send),
                contentDescription = stringResource(id = R.string.send_link_button)
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LinkListToolbar() {
    TopAppBar(
        title = { Text(stringResource(id = R.string.my_links)) },
    )
}
