package ru.datana.smart.ui.converter.mock.app

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    routing {
        static("/") {
            defaultResource("static/index.html")
            resources("static")
        }
    }
    get("/list") {
        call.responseText("""
        {
          "cases": [
            {"name": "Case1", "dir": "Case1"}
          ]
        }
        """.trimIndent())
    }
    get("/send") {
        val case = call.parameter["case"] ?: throw BadQueryException("No case is specified")
        val meltInfo = objectMapper.readValue(File("$pathToCatalog/$case/meta.json"), ConverterMeltInfo::class.java)
        val timeStart = Instant.now()
        val meltId = "${meltInfo.meltNumber}-${timeStart.toEpochMilli()}"
        val meltInfoInit = meltInfo.copy(
            id = meltId,
            timeStart = timeStart
        )
        kafkaProducer.send(meltId, objectMapper.writeValueAsString(meltInfoInit))
        call.responseOk()
    }
}
