package ru.datana.smart.ui.temperature.app

import com.typesafe.config.ConfigFactory
import io.ktor.config.HoconApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import io.ktor.util.KtorExperimentalAPI
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.assertEquals

internal class ApplicationKtTest {

    @KtorExperimentalAPI
    @Test
    fun testRoot() {
        with(engine) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    companion object {

        @KtorExperimentalAPI
        val engine = TestApplicationEngine(createTestEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application-test.conf"))
        })

        @KtorExperimentalAPI
        @BeforeClass
        @JvmStatic fun setup(){
            engine.start(wait = false)
        }
    }
}
