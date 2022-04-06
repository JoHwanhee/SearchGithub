package com.hwanhee.search_github
import com.google.gson.Gson
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

@OptIn(ExperimentalKotest::class)
class GithubApiTest : FunSpec() {
    private lateinit var server: MockWebServer

    override fun beforeSpec(spec: Spec) {
        server = MockWebServer()
        server.start()
    }

    override fun afterSpec(spec: Spec) {
        server.shutdown()
    }

    init {
        testCoroutineDispatcher = true

        test("API 호출 기본 테스트 - 200 리턴 되어야한다") {
            val api = TestHelper.provideGithubApi(server.url("/"))
            server.enqueueResponse("githubapi-base-200.json", 200)

            // when
            val res = api.search("")

            res.isSuccessful shouldBe true
            res.code() shouldBe 200
        }
    }
}