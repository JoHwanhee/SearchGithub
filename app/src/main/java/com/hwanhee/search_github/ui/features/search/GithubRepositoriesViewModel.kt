package com.hwanhee.search_github.ui.features.search

import androidx.lifecycle.viewModelScope
import com.hwanhee.search_github.base.BaseViewModel
import com.hwanhee.search_github.base.lessThan
import com.hwanhee.search_github.base.lessThanOrEquals
import com.hwanhee.search_github.model.ui.RepositoryUIItems
import com.hwanhee.search_github.model.vo.RequestPage
import com.hwanhee.search_github.model.vo.SearchWord
import com.hwanhee.search_github.repository.GithubSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GithubRepositoriesViewModel @Inject constructor(
    private val repository: GithubSearchRepository
) : BaseViewModel<
        GithubRepositoriesContract.Event,
        GithubRepositoriesContract.State,
        GithubRepositoriesContract.Effect>() {

    private var _lastRequestedPageCache: RequestPage = RequestPage()
    private var _lastSearchWord = SearchWord()
    private var _currentItem: RepositoryUIItems = RepositoryUIItems()

    override fun handleEvents(event: GithubRepositoriesContract.Event) {
        when (event) {

            /** 아이템 선택 됐을 때 **/
            is GithubRepositoriesContract.Event.ItemSelected -> {
                selectItemEffect(event.url)
            }

            /** 검색 모드 진입 버튼 클릭할 때 **/
            is GithubRepositoriesContract.Event.SearchOn -> {
                updateSearchTextState("")
                reallocateCacheData()
                updateSearchModeState(true)
            }

            /** 검색 모드 나가기 버튼 클릭할 때 **/
            is GithubRepositoriesContract.Event.SearchOff -> {
                updateSearchTextState("")
                reallocateCacheData()
                updateSearchModeState(false)
            }

            /** 검색어 입력될 떄 **/
            is GithubRepositoriesContract.Event.SearchTextChanged -> {
                updateSearchTextState(event.value)
            }

            /** 검색 동작할 때 (키보드 확인 버튼) **/
            is GithubRepositoriesContract.Event.Searched -> {
                reallocateCacheData()
                search(event.userSearchWord)
            }

            /** 스크롤이 바닥을 쳤을 때 **/
            is GithubRepositoriesContract.Event.ScrollMeetsBottom -> {
                if (isCurrentSearching() && increaseIfNeedMore()){
                    search(_lastSearchWord)
                }
            }
        }
    }

    private fun search(searchWord: SearchWord) {
        val scope = viewModelScope
        val repository = this.repository
        val currentUIItems = _currentItem
        val searchPage = _lastRequestedPageCache

        search(scope, repository, currentUIItems, searchWord, searchPage)
    }

    private fun search(
        coroutineScope: CoroutineScope,
        repository: GithubSearchRepository,
        currentUIItems: RepositoryUIItems,
        word: SearchWord,
        paging: RequestPage) {
        if (checkParams(word, paging)) return

        setLoadingState(paging)

        coroutineScope.launch {
            repository.searchRepository(word, paging)
                .catch {
                    errorEffect()
                }
                .collect { newUiItems ->
                    if(newUiItems is RepositoryUIItems) {
                        val currentItem = replaceOrMerge(currentUIItems, newUiItems, paging)
                        setLoadingState(false)
                        updateUIItems(currentItem)
                        setLastSearchWord(word)
                        setTotal(currentItem.total)
                    }
                    else {
                        errorEffect()
                    }
                }
        }
    }

    private fun replaceOrMerge(
        currentUIItems: RepositoryUIItems,
        newUiItems: RepositoryUIItems,
        requestPage: RequestPage
    ) = if (requestPage lessThanOrEquals 1)
            newUiItems
        else
            currentUIItems + newUiItems

    /********************* States *******************/
    override fun setInitialState()
    = GithubRepositoriesContract.State()

    private fun updateSearchModeState(mode: Boolean)
    = setState {
        copy(isSearchOpened = mode)
    }

    private fun setLoadingState(paging: RequestPage) {
        val isLoading = paging.page == 1
        val isLoadingMore = !isLoading
        setLoadingState(isLoading, isLoadingMore)
    }

    private fun setLoadingState(isLoading: Boolean, isLoadingMore: Boolean = false)
    = setState {
        copy(isLoading = isLoading,
             isLoadingMore = isLoadingMore)
    }

    private fun updateUIItems(uiItems: RepositoryUIItems)
    = setState {
        copy(repositoryUIItems = uiItems)
    }

    private fun updateSearchTextState(value: String)
    = setState {
        copy(searchText= value)
    }

    /********************* Effects *******************/
    private fun selectItemEffect(url: String)
    = setEffect {
        GithubRepositoriesContract.Effect.Navigation.ToItemDetails(url)
    }

    private fun errorEffect()
    = setEffect {
        GithubRepositoriesContract.Effect.DataError
    }

    /********************* Helpers *******************/
    private fun checkParams(
        word: SearchWord,
        paging: RequestPage
    ) = word.isEmpty() || (paging lessThan 1)

    private fun setLastSearchWord(searchWord: SearchWord) {
        _lastSearchWord = searchWord
    }

    private fun setTotal(it: Int) {
        _lastRequestedPageCache.total = it
    }

    /**
     * 캐시에 사용하는 데이터들을 재할당해준다.
     *  -> 객체 재활용시 버그 유발 가능성이 높음
     * */
    private fun reallocateCacheData() {
        _currentItem = RepositoryUIItems()
        _lastRequestedPageCache = RequestPage()
        _lastSearchWord = SearchWord()
    }

    private fun increaseIfNeedMore() : Boolean {
        return _lastRequestedPageCache.increaseIfNeedMore()
    }

    private fun isCurrentSearching() : Boolean {
        var searchMode = false
        currentState {
            searchMode = isSearchOpened
        }
        return searchMode
    }
}
