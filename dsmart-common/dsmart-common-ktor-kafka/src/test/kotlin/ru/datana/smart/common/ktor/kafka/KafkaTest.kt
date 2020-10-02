package ru.datana.smart.common.ktor.kafka

import io.ktor.server.testing.*
import kotlin.test.Test

internal class KafkaTest {

    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
//            handleRequest(HttpMethod.Get, "/").apply {
//                assertEquals(HttpStatusCode.OK, response.status())
////                assertEquals("HELLO WORLD!", response.content)
//            }
        }
    }
}

