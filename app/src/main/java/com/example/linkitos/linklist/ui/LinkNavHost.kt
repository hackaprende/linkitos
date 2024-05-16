package com.example.linkitos.linklist.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.linkitos.models.Link

private const val LAST_LINK_DESTINATION = "lastLinkScreen"
private const val LINK_LIST_DESTINATION = "linkListScreen"

@Composable
fun LinkNavHost(
    onLinkClicked: (link: Link) -> Unit,
    linkListViewModel: LinkListViewModel = hiltViewModel()
) {

    val navController = rememberNavController()

    NavHost(
        startDestination = LAST_LINK_DESTINATION,
        navController = navController,
    ) {
        composable(LAST_LINK_DESTINATION) {
            LastLinkScreen(
                onLinkClicked,
                onGoToListButtonClicked = {
                    navController.navigate(LINK_LIST_DESTINATION)
                },
                linkListViewModel
            )
        }

        composable(LINK_LIST_DESTINATION) {
            LinkListScreen(
                onLinkClicked,
                linkListViewModel
            )
        }
    }
}
