package com.hwanhee.search_github
import com.google.gson.Gson
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer

class TestDispatcherTest : FunSpec() {
    private lateinit var server: MockWebServer

    override fun beforeSpec(spec: Spec) {
        server = MockWebServer()
        server.start()
    }

    override fun afterSpec(spec: Spec) {
        server.shutdown()
    }

    init {
        test("API 호출 기본 테스트").config(testCoroutineDispatcher = true) {
            val api = TestHelper.provideGithubApi(server.url("/"))

            val response = MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(200)
                .setBody(Gson().toJson("{}"))

            server.enqueue(response)

            val res = api.search("")

            res.isSuccessful shouldBe true
        }
    }
}