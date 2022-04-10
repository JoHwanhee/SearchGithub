package com.hwanhee.search_github.ui.github_repositories

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
                updateSearchModeState(true)
            }

            /** 검색 모드 나갈 때 **/
            is GithubRepositoriesContract.Event.SearchOff -> {
                updateSearchTextState("")
                allocateCacheData()
                updateSearchModeState(false)
            }

            /** 검색어 입력될 떄 **/
            is GithubRepositoriesContract.Event.SearchTextChanged -> {
                updateSearchTextState(event.value)
            }

            /** 검색 동작할 때 (키보드 확인 버튼) **/
            is GithubRepositoriesContract.Event.Searched -> {
                allocateCacheData()

                val currentUIItems = _currentItem
                val searchWord = event.searchWord
                val searchPage = _lastRequestedPageCache
                searchRepository(currentUIItems, searchWord, searchPage)
                setLastSearched(searchWord)
            }

            /** 스크롤이 바닥을 쳤을 때 **/
            is GithubRepositoriesContract.Event.ScrollMeetsBottom -> {
                if (isCurrentSearching() and increaseIfNeedMore()){
                    val currentUIItems = _currentItem
                    val searchWord = _lastSearchedCache
                    val searchPage = _lastRequestedPageCache
                    searchRepository(currentUIItems, searchWord, searchPage)
                }
            }

        }
    }

    private fun searchRepository(currentUIItems: RepositoryUIItems, word: SearchWord, paging: RequestPage) {
        if (word.isEmpty() or (paging lessThan 1)) return

        updateUIItems(currentUIItems)
        val isLoading = paging.page == 1
        val isLoadingMore = !isLoading
        setLoadingState(isLoading, isLoadingMore)

        viewModelScope.launch {
            repository.searchRepository(word, paging)
                .catch {
                    errorEffect()
                }
                .collect { res ->
                    if(res is RepositoryUIItems) {
                        val currentItem = addOrCreateUiItems(currentUIItems, paging, res)
                        setLoadingState(false)
                        updateUIItems(currentItem)
                        setTotal(res)
                    }
                    else {
                        errorEffect()
                    }
                }
        }
    }

    private fun addOrCreateUiItems(
        currentUIItems: RepositoryUIItems,
        paging: RequestPage,
        res: RepositoryUIItems
    ) = if (paging lessThanOrEquals 1) res else currentUIItems.let { currentUIItems.plus(res) }

    /********************* States *******************/
    override fun setInitialState()
    = GithubRepositoriesContract.State()

    private fun updateSearchModeState(mode: Boolean)
    = setState {
        copy(isSearchOpened = mode)
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
    private fun selectItemEffect(url: String) =
        setEffect {
            GithubRepositoriesContract.Effect.Navigation.ToItemDetails(url)
        }

    private fun errorEffect() =
        setEffect {
            GithubRepositoriesContract.Effect.DataError
        }

    /********************* Helpers *******************/
    private fun setLastSearched(searchWord: SearchWord) {
        _lastSearchedCache = searchWord
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

    private fun isCurrentSearching() : Boolean {
        var searchMode = false
        currentState {
            searchMode = isSearchOpened
        }
        return searchMode
    }
}
