package com.hwanhee.search_github.model.vo

class SearchWord(
    val keyword: String = "",
    val language: String = ""
) {
    val isExtensionSearch = language.isNotEmpty()

    override fun toString(): String {
        if(language.isEmpty())
            return keyword

        return "${keyword}+language:${language}"
    }

    fun isEmpty(): Boolean {
        return keyword.isEmpty()
    }
}