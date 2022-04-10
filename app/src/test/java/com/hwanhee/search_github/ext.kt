package com.hwanhee.search_github

import androidx.compose.ui.graphics.Color
import com.hwanhee.search_github.base.hashColor
import com.hwanhee.search_github.base.parse
import com.hwanhee.search_github.base.sha256
import com.hwanhee.search_github.model.vo.SearchWord
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import okhttp3.internal.toHexString
import java.lang.StringBuilder

internal class ExtTest : StringSpec({
    "스트링 해시 컬러 테스트" {
        "tetris".hashColor shouldNotBe null
        "tetris".hashColor.value shouldBe Color.parse("#a10335").value
    }
})