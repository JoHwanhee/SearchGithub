package com.hwanhee.search_github.model.vo

class SearchWord(
    private val keyword: String,
    private val language: String = ""
) {
    override fun toString(): String {
        if(language.isEmpty())
            return keyword

        return "${keyword}+language:${language}"
    }
}