package com.hwanhee.search_github.ui.github_repositories

import com.hwanhee.search_github.base.ViewEvent
import com.hwanhee.search_github.base.ViewSideEffect
import com.hwanhee.search_github.base.ViewState
import com.hwanhee.search_github.model.ui.RepositoryUIItems
import com.hwanhee.search_github.model.vo.SearchWord

class GithubRepositoriesContract {
    // UI Events: 유저의 행동으로 발생하는 이벤트들을 정의한다.
    sealed class Event : ViewEvent {
        object SearchOn : Event()
        object SearchOff : Event()
        object ScrollMeetsBottom : Event()
        data class Searched(val searchWord: SearchWord) : Event()
        data class ItemSelected(val url: String) : Event()
        data class SearchTextChanged(val value: String) : Event()
    }

    // 해당 화면의 현재 상태(데이터)들을 정의한다.
    data class State(val repositoryUIItems: RepositoryUIItems? = null,
                     val isSearchOpened: Boolean = false,
                     val isLoadingMore: Boolean = false,
                     val isLoading: Boolean = false) : ViewState

    // ViewModel에서 UI로 전달하는 사이드 이펙트들을 정의한다.
    sealed class Effect : ViewSideEffect {
        object DataError : Effect()

        sealed class Navigation : Effect() {
            data class ToItemDetails(val url: String) : Navigation()
        }
    }
}