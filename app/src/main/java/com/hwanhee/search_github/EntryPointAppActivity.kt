package com.hwanhee.search_github

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hwanhee.search_github.ui.github_repositories.GithubRepositoriesContract
import com.hwanhee.search_github.ui.github_repositories.GithubRepositoriesScreen
import com.hwanhee.search_github.ui.github_repositories.GithubRepositoriesViewModel
import com.hwanhee.search_github.ui.theme.Search_githubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntryPointAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Search_githubTheme {
                SearchGithubApp()
            }
        }
    }
}

@Composable
private fun SearchGithubApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = NavigationKeys.Route.REPO_LIST) {
        composable(route = NavigationKeys.Route.REPO_LIST) {
            GithubRepositoriesDestination()
        }
    }
}

@Composable
private fun GithubRepositoriesDestination() {
    val viewModel: GithubRepositoriesViewModel = hiltViewModel()
    val state = viewModel.viewState.value
    val searchTextState = viewModel.searchTextState.value
    val uriHandler = LocalUriHandler.current

    GithubRepositoriesScreen(
        state = state,
        searchTextState = searchTextState,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is GithubRepositoriesContract.Effect.Navigation.ToItemDetails) {
                uriHandler.openUri(navigationEffect.url)
            }
    })
}

object NavigationKeys {
    object Route {
        const val REPO_LIST = "REPO_LIST"
    }
}