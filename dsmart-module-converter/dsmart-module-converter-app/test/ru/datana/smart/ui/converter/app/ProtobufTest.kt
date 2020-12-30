package ru.datana.smart.ui.converter.app

import com.google.protobuf.ByteString
import org.junit.Test
import ru.datana.smart.converter.transport.math.ConverterTransportMlUiOuterClass
import kotlin.test.assertEquals

class ProtobufTest {
    @Test
    fun protoTest() {
        val mo: ConverterTransportMlUiOuterClass.ConverterTransportMlUi = ConverterTransportMlUiOuterClass.ConverterTransportMlUi.newBuilder()
            .setFrameId("123")
            .setFrame(ByteString.copyFrom(ByteArray(5) { i -> i.toByte() }))
            .build()

        val ba: ByteArray = mo.toByteArray()

        val dmo: ConverterTransportMlUiOuterClass.ConverterTransportMlUi = ConverterTransportMlUiOuterClass.ConverterTransportMlUi.parseFrom(ba)
        println(dmo.frame)
        assertEquals("123", dmo.frameId)

    }
}
