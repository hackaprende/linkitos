package com.example.linkitos.linklist.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.linkitos.ErrorDialog
import com.example.linkitos.LoadingWheel
import com.example.linkitos.R
import com.example.linkitos.api.ApiResponseStatus
import com.example.linkitos.models.Link

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkListScreen(
    onLinkClicked: (link: Link) -> Unit,
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
                links = state.links,
                onLinkClicked = onLinkClicked,
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
    links: List<Link>,
    onLinkClicked: (link: Link) -> Unit,
) {

    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.padding_regular),
                    end = dimensionResource(id = R.dimen.padding_regular),
                    top = dimensionResource(id = R.dimen.padding_medium)
                ),
            text = stringResource(id = R.string.recently_shortened_links),
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

        if (links.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.link_list_empty_view_spacing),
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.link_list_empty_view),
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LinkList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(id = R.dimen.padding_regular)),
                links = links,
                onLinkClicked = onLinkClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LinkListToolbar() {
    TopAppBar(
        title = { Text(stringResource(id = R.string.my_links)) },
        navigationIcon = {
            // TODO - Go back when clicking
        }
    )
}

/***
 * combinedClickable is still experimental but stable accordingly to Documentation:
 * https://developer.android.com/jetpack/compose/touch-input/pointer-input/tap-and-press#long-press-show
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LinkList(
    modifier: Modifier,
    links: List<Link>,
    onLinkClicked: (link: Link) -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    LazyColumn(
        modifier = modifier
    ) {
        items(links) { link ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            onLinkClicked(link)
                        },
                        onLongClick = {
                            clipboardManager.setText(AnnotatedString((link.short)))
                        },
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_regular))
                        .semantics { testTag = "link-${link.short}" },
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
        }
    }
}
