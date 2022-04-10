package com.hwanhee.search_github.model.vo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

internal class SearchWordTest : StringSpec({
    "keyword: tetris, language:assembly" {
        SearchWord("tetris", "assembly").toString() shouldBe "tetris+language:assembly"
    }

    "언어를 빈칸으로 줬을 때" {
        SearchWord("tetris", "").toString() shouldBe "tetris"
        SearchWord("tetris").toString() shouldBe "tetris"
    }
})