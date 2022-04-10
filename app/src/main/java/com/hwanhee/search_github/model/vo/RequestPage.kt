package com.hwanhee.search_github.model.vo

data class RequestPage (
    private var currentPage: Int = 1,
    var total: Int = 0,
    val perPage: Int = 20,
) {
    val page get() = currentPage

    fun increaseIfNeedMore() : Boolean {
        val resNeedMore = needMore()
        if (resNeedMore) {
            currentPage += 1
        }

        return resNeedMore
    }

    private fun needMore() : Boolean{
        return total > currentPage * perPage
    }
}