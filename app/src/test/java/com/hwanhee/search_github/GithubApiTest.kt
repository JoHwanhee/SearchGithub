package com.hwanhee.search_github
import com.google.gson.Gson
import com.hwanhee.search_github.model.ResultWrapper
import com.hwanhee.search_github.model.dto.RepositoryResponseDto
import com.hwanhee.search_github.model.safeApiCall
import com.hwanhee.search_github.model.vo.SearchWord
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.coroutines.Dispatchers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.util.*

@OptIn(ExperimentalKotest::class)
class GithubApiTest : BehaviorSpec() {
    private lateinit var server: MockWebServer
    private lateinit var testHelper: TestHelper
    private lateinit var api: GithubApi

    override fun beforeSpec(spec: Spec) {
        server = MockWebServer()
        server.start()

        //testHelper = TestHelperRealServer(Dispatchers.IO)
        testHelper = TestHelperMock(Dispatchers.IO, server.url("/"))
        api = testHelper.provideGithubApi()
    }

    override fun afterSpec(spec: Spec) {
        server.shutdown()
    }

    init {
        testCoroutineDispatcher = true

        given("사용자 검색 테스트") {
            `when`("q=test page=1 per_page=20") {
                server.enqueueResponse("q=test_page=1_perpage=20.json", 200)
                val res = api.search("test", 1, 20)

                then("200 response 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null
                }

                then("items 개수 20개 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success

                    resultWrapper shouldNotBe null
                    resultWrapper.value.items.count() shouldBe 20
                }
            }

            `when`("q=test page=1 per_page=1") {
                server.enqueueResponse("q=test_page=1_perpage=1.json", 200)
                val res = api.search("test", 1, 1)

                then("200 response 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null
                }

                then("items 개수 1개 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null

                    resultWrapper.value.let {
                        it.items.count() shouldBe 1
                    }
                }
            }

            `when`("q=test page=1 per_page=-1") {
                server.enqueueResponse("q=test_page=1_perpage=20.json", 200)
                val res = api.search("test", 1, -1)

                then("200 response 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null
                }

                then("items 개수 기본값으로 변경되어 20개 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null
                    resultWrapper.value.items.count() shouldBe 20
                }
            }

            `when`("q=test page=1 per_page=0") {
                server.enqueueResponse("q=test_page=1_perpage=20.json", 200)
                val res = api.search("test", 1, 0)

                then("200 response 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null
                }

                then("items 개수 기본값으로 변경되어 20개 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null
                    resultWrapper.value.items.count() shouldBe 20
                }
            }

            `when`("q=tetris+language:assembly page=1 per_page=20") {
                server.enqueueResponse("q=tetris+language:assembly_page=1_perpage=20.json", 200)
                val res = api.search(SearchWord("tetris", "assembly"), 1, 20)

                then("200 response 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null
                }

                then("items 개수 20개 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null
                    resultWrapper.value.items.count() shouldBe 20
                }

                then("Assembly 언어만 검색 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Success
                    resultWrapper shouldNotBe null
                    resultWrapper.value.items.count() shouldBe 20
                    resultWrapper.value.items.forEach { item ->
                        item shouldNotBe null
                        item.language shouldBe "Assembly"
                    }
                }
            }

            `when`("검색어가 빈칸일 때") {
                server.enqueueResponse("githubapi-error-422.json", 422)
                val res = api.search("", 1, 20)

                then("422 response 되어야한다.") {
                    val resultWrapper = res as ResultWrapper.Error
                    resultWrapper shouldNotBe null
                    resultWrapper.code shouldBe 422
                }
            }
        }
    }
}