package com.hwanhee.search_github.model.vo

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class SearchWordTest : StringSpec({
    "keyword: tetris, language:assembly" {
        SearchWord("tetris", "assembly").toString() shouldBe "tetris+language:assembly"
    }
})