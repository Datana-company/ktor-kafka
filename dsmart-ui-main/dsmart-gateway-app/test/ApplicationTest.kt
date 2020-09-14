package ru.datana.smart.ui

import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @OptIn(KtorExperimentalAPI::class)
    @Test
    fun testRoot() {
        withTestApplication({
            module(testing = true)
//            (environment.config as MapApplicationConfig).apply {
//                put("ktor.kafka.bootstrap.servers", listOf("172.29.40.58:9092"))
////                put("ktor.kafka.bootstrap.servers", listOf(System.getenv("KAFKA_BOOTSTRAP_SERVER")))
////                put("youkube.upload.dir", tempPath.absolutePath)
//            }
        }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
