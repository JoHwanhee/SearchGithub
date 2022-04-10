package com.hwanhee.search_github.model.vo


import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe


internal class RequestPageTest : BehaviorSpec() {
    init {
        given("페이지네이션 테스트") {
            `when`("currentPage = 1, total = 10, perPage = 5, 한번 페이지 증가한 경우") {
                val page = RequestPage(currentPage = 1,total = 10, perPage = 5)
                val canNeedMore = page.increaseIfNeedMore()

                then("NeedMore 값이 True여야한다.") {
                    canNeedMore shouldBe true
                }

                then("2페이지가 되어야 한다.") {
                    page.page shouldBe 2
                }
            }

            `when`("currentPage = 1, total = 2, perPage = 5, 두 번 페이지 증가한 경우") {
                val page = RequestPage(currentPage = 1,total = 10, perPage = 5)
                var canNeedMore = page.increaseIfNeedMore()

                then("첫번째는 NeedMore 값이 True여야한다.") {
                    canNeedMore shouldBe true
                }

                then("2페이지가 되어야 한다.") {
                    page.page shouldBe 2
                }

                canNeedMore = page.increaseIfNeedMore()

                then("두번째는 NeedMore 값이 False여야한다.") {
                    canNeedMore shouldBe false
                }

                then("위와 동일하게 2페이지가 유지 되어야 한다.") {
                    page.page shouldBe 2
                }
            }
        }
    }
}
