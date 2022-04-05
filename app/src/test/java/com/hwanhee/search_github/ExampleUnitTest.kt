package com.hwanhee.search_github

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.junit.Test

import org.junit.Assert.*

class MyTests : FunSpec(
    {
        test("샘플 스트링 테스트")
        {
            "sample".length shouldBe 6
            "".length shouldBe 0
        }
    }
)

