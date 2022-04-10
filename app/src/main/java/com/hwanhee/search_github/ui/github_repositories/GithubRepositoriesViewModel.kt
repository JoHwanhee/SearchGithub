package com.hwanhee.search_github.ui.github_repositories

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.hwanhee.search_github.base.BaseViewModel
import com.hwanhee.search_github.base.lessThan
import com.hwanhee.search_github.base.lessThanOrEquals
import com.hwanhee.search_github.model.ui.RepositoryUIItems
import com.hwanhee.search_github.model.vo.RequestPage
import com.hwanhee.search_github.model.vo.SearchWord
import com.hwanhee.search_github.repository.GithubSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private var _lastSearchedCache = SearchWord()
    private var _currentItem: RepositoryUIItems = RepositoryUIItems()

    /** bindings */
    private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    override fun handleEvents(event: GithubRepositoriesContract.Event) {
        when (event) {

            /** 아이템 선택 됐을 때 **/
            is GithubRepositoriesContract.Event.ItemSelected -> {
                selectItemEffect(event.url)
            }

            /** 검색 모드 진입될 때 **/
            is GithubRepositoriesContract.Event.SearchOn -> {
                updateSearchTextState("")
                allocateCacheData()
                setSearchOnState()
            }

            /** 검색 모드 나갈 때 **/
            is GithubRepositoriesContract.Event.SearchOff -> {
                updateSearchTextState("")
                allocateCacheData()
                setSearchOffState()
            }

            /** 검색어 입력될 떄 **/
            is GithubRepositoriesContract.Event.SearchTextChanged -> {
                updateSearchTextState(event.value)
            }

            /** 검색 동작할 때 (키보드 확인 버튼) **/
            is GithubRepositoriesContract.Event.Searched -> {
                allocateCacheData()

                val searchWord = event.searchWord
                val searchPage = _lastRequestedPageCache
                searchRepository(searchWord, searchPage)
                setCache(event)
            }

            /** 스크롤이 바닥을 쳤을 때 **/
            is GithubRepositoriesContract.Event.ScrollMeetsBottom -> {
                if (isCurrentSearching() and increaseIfNeedMore()){
                    val searchWord = _lastSearchedCache
                    val searchPage = _lastRequestedPageCache
                    searchRepository(searchWord, searchPage)
                }
            }

        }
    }

    private fun searchRepository(word: SearchWord, paging: RequestPage) {
        if (word.isEmpty() or (paging lessThan 1)) return

        setSearchStartsState()

        viewModelScope.launch {
            repository.searchRepository(word, paging)
                .catch {
                    errorEffect()
                }
                .collect { res ->
                    if(res is RepositoryUIItems) {
                        setCurrentItem(paging, res)
                        setSearchEndState()
                        setTotal(res)
                    }
                    else {
                        errorEffect()
                    }
                }
        }
    }

    private fun setCurrentItem(
        paging: RequestPage,
        res: RepositoryUIItems
    ) {
        _currentItem =
            if (paging lessThanOrEquals 1) res else _currentItem.let { _currentItem.plus(res) }
    }

    /********************* States *******************/
    override fun setInitialState() =
        GithubRepositoriesContract.State(
            repositoryUIItems = _currentItem,
            isLoading = false,
            isSearchOpened = false
        )

    private fun setSearchOnState() =
        setState {
            GithubRepositoriesContract.State(isSearchOpened = true)
        }

    private fun setSearchOffState() {
        setState {
            copy(isSearchOpened = false)
        }
    }

    private fun setSearchStartsState()
    = setState {
        copy(
            repositoryUIItems = _currentItem,
            isLoading = _lastRequestedPageCache.page == 1,
            isLoadingMore = _lastRequestedPageCache.page != 1)
    }

    private fun setSearchEndState()
    = setState {
        copy(
            repositoryUIItems = _currentItem,
            isLoading = false,
            isLoadingMore = false)
    }

    /********************* Effects *******************/
    private fun selectItemEffect(url: String) =
        setEffect {
            GithubRepositoriesContract.Effect.Navigation.ToItemDetails(url)
        }

    private fun errorEffect() =
        setEffect {
            GithubRepositoriesContract.Effect.DataError
        }

    /********************* Helpers *******************/
    private fun setCache(event: GithubRepositoriesContract.Event.Searched) {
        _lastSearchedCache = event.searchWord
    }

    private fun setTotal(it: RepositoryUIItems) {
        _lastRequestedPageCache.total = it.total
    }

    /**
     * 캐시에 사용하는 데이터들을 재할당해준다.
     *  -> 객체 재활용시 버그 유발 가능성이 높음
     * */
    private fun allocateCacheData() {
        _currentItem = RepositoryUIItems()
        _lastRequestedPageCache = RequestPage()
        _lastSearchedCache = SearchWord()
    }

    private fun increaseIfNeedMore() : Boolean {
        return _lastRequestedPageCache.increaseIfNeedMore()
    }

    private fun updateSearchTextState(value: String) {
        _searchTextState.value = value
    }

    private fun isCurrentSearching() : Boolean {
        var searchMode = false
        currentState {
            searchMode = isSearchOpened
        }
        return searchMode
    }
}
