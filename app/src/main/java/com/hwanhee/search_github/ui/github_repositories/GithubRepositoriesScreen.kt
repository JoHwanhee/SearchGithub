package com.hwanhee.search_github.ui.github_repositories

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.hwanhee.search_github.R
import com.hwanhee.search_github.base.LAUNCH_LISTEN_FOR_EFFECTS
import com.hwanhee.search_github.base.hashColor
import com.hwanhee.search_github.model.ui.RepositoryUIItem
import com.hwanhee.search_github.model.ui.RepositoryUIItems
import com.hwanhee.search_github.model.vo.SearchWord
import com.hwanhee.search_github.ui.InfiniteListHandler
import com.hwanhee.search_github.ui.defaultCoilBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


@Composable
fun GithubRepositoriesScreen(
    state: GithubRepositoriesContract.State,
    searchTextState: String,
    effectFlow: Flow<GithubRepositoriesContract.Effect>?,
    onEventSent: (event: GithubRepositoriesContract.Event) -> Unit,
    onNavigationRequested: (navigationEffect: GithubRepositoriesContract.Effect.Navigation) -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val listState: LazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val networkError = stringResource(id = R.string.network_not_smooth)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(LAUNCH_LISTEN_FOR_EFFECTS) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is GithubRepositoriesContract.Effect.Navigation.ToItemDetails -> onNavigationRequested(
                    effect
                )
                else -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = networkError,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }?.collect()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MainAppBar(
                state = state,
                searchTextState = searchTextState,
                searchTextFocus = focusRequester,
                onTextChange = {
                    onEventSent(GithubRepositoriesContract.Event.SearchTextChanged(it))
                },
                onSearchClicked = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                        onEventSent(GithubRepositoriesContract.Event.Searched(SearchWord(it)))
                        focusManager.clearFocus()
                    }
                },
                onCloseClicked = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                        onEventSent(GithubRepositoriesContract.Event.SearchOff)
                    }
                },
                onSearchTriggered = {
                    onEventSent(GithubRepositoriesContract.Event.SearchOn)
                }
            )
        },
    ) {
        Box {
            when {
                state.isLoading -> LoadingBar()
                state.repositoryUIItems?.isEmpty ?: true -> {
                    NoItem()
                }
                else -> {
                    RepositoryList(
                        listState = listState,
                        repositoryUIItems = state.repositoryUIItems,
                        isLoadingMoreState = state.isLoadingMore,
                        onItemClicked = { itemId -> onEventSent(GithubRepositoriesContract.Event.ItemSelected(itemId))
                        },
                        onScrollInProgress = {
                            focusManager.clearFocus()
                        },
                        onScrollMeetsBottom = {
                            onEventSent(GithubRepositoriesContract.Event.ScrollMeetsBottom)
                        }
                    )

                }
            }
        }
    }
}

@Composable
fun NoItem() {
    Image(
        painterResource(R.drawable.ic_baseline_search_off_24),
        contentDescription = "",
        modifier = Modifier
            .fillMaxSize()
            .padding(150.dp)
    )
}

@Composable
fun StarIcon() {
    Icon(
        painterResource(R.drawable.ic_baseline_star_outline_24),
        contentDescription = "",
        modifier = Modifier
            .width(16.dp)

    )
}

@Composable
fun MainAppBar(
    state: GithubRepositoriesContract.State,
    searchTextState: String,
    searchTextFocus: FocusRequester,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit
) {
    if(state.isSearchOpened) {
        SearchAppBar(
            text = searchTextState,
            searchTextFocus= searchTextFocus,
            onTextChange = onTextChange,
            onCloseClicked = onCloseClicked,
            onSearchClicked = onSearchClicked
        )
    }
    else {
        DefaultAppBar {
            onSearchTriggered.invoke()
        }
    }
}

@Composable
fun CircleShape(color: Color, shape: Shape = CircleShape){
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(shape)
            .background(color)
    )
}

@Composable
fun SearchAppBar(
    text: String,
    searchTextFocus: FocusRequester,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth()
            .focusRequester(searchTextFocus),
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.search),
                    color = Color.White
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    onClick = {
                        onCloseClicked.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "close icon",
                        tint = Color.White
                    )
                }
            },
            trailingIcon =  {
                if(text.isNotEmpty())
                    IconButton(
                        onClick = {
                            onTextChange("")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "clear Icon",
                            tint = Color.White
                        )
                    }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
            ))
    }
    LaunchedEffect(Unit) {
        searchTextFocus.requestFocus()
    }
}

@Composable
fun DefaultAppBar(onSearchClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_bar_title)
            )
        },
        actions = {
            IconButton(
                onClick = { onSearchClicked() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun RepositoryList(
    listState: LazyListState,
    repositoryUIItems: RepositoryUIItems?,
    isLoadingMoreState: Boolean,
    onItemClicked: (url: String) -> Unit = { },
    onScrollInProgress: () -> Unit = { },
    onScrollMeetsBottom: () -> Unit = { }
) {
    LazyColumn(
        state= listState,
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        repositoryUIItems?.let {
            items(repositoryUIItems.items) { item ->
                ItemRow(item = item, onItemClicked = onItemClicked)
                Divider()
            }
        }

        if(repositoryUIItems?.items?.isNullOrEmpty() != false) {
            item {
                NoItem()
            }
        }

        if (isLoadingMoreState) {
            item {
                LoadingBar()
            }
        }
    }

    if(listState.isScrollInProgress) {
        onScrollInProgress.invoke()
    }

    InfiniteListHandler(listState = listState) {
        onScrollMeetsBottom.invoke()
    }
}

@Composable
fun ItemRow(
    item: RepositoryUIItem,
    onItemClicked: (url: String) -> Unit = { }
) {
    Column(modifier = Modifier
        .animateContentSize()
        .fillMaxWidth()
        .clickable { onItemClicked(item.htmlUrl) }
        .padding(16.dp)
    ) {
        OwnersItemRow(item.ownerUIItem.name, item.ownerUIItem.avatarUrl)

        RepositoryItemDetails(
            item = item,
            expandedLines = 2,
            modifier = Modifier
                .fillMaxWidth(0.80f)
        )
    }
}

@Composable
fun RepositoryItemDetails(
    item: RepositoryUIItem?,
    expandedLines: Int,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        item?.let {
            Text(
                text = item.fullName,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = item.description.trim(),
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.caption,
                    maxLines = expandedLines
                )
            }

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                EtcInformationRow(item)
            }
        }
    }
}

@Preview
@Composable
fun OwnersItemRowPreview() {
    OwnersItemRow("테스트", "https://avatars.githubusercontent.com/u/19929?v=4")
}

@Composable
fun OwnersItemRow(
    ownerName: String,
    thumbnailUrl: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically
    ) {
        OwnersThumbnail(thumbnailUrl)

        Spacer(modifier = Modifier.padding(2.dp))

        Text(text = ownerName.trim(),
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1,
            maxLines = 1)
    }
}

@Composable
fun EtcInformationRow(
    item: RepositoryUIItem
) {
    Row(verticalAlignment = Alignment.CenterVertically
    ) {
        StarIcon()
        Spacer(modifier = Modifier.padding(2.dp))
        Text(text = item.stargazersCount.toString(),
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            maxLines = 1)

        Spacer(modifier = Modifier.padding(4.dp))

        CircleShape(color = item.language.hashColor)

        Spacer(modifier = Modifier.padding(2.dp))

        Text(text = item.language.toString(),
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            maxLines = 1)


    }
}

@Composable
fun OwnersThumbnail(
    thumbnailUrl: String,
) {
    Image(
        painter = rememberImagePainter(
            data = thumbnailUrl,
            builder = defaultCoilBuilder()
        ),
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape),
        contentDescription = "thumbnail",
    )
}

@Composable
fun LoadingBar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}