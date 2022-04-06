package com.hwanhee.search_github.model.vo

class SearchWord(
    val keyword: String,
    val language: String
) {
    override fun toString(): String {
        return "${keyword}+language:${language}"
    }
}