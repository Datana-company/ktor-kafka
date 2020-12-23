package ru.datana.smart.ui.converter.backend

import kotlinx.coroutines.runBlocking
import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SignalerNKR1130Test {
    /** NKR-1130
     * ����������� ��������� �������� �� ����������� �����.
     * ���� �� ��������� ������ � ���� ��������� ������������ � ���� ������, � ����� �� ��������� ����� ������ ���
     * ������������ �� �������� �������� ����, �������� �� ������ ���� ����� �� ����� � ������� ������� �� ������ ������.
     * ��� �� �����, �� ����� , ��� ������ ��� ���� ����������.
     */
    @Test
    fun signalerTestCase1NKR1130() {
        runBlocking {
            val timeStart = Instant.now()

            val repository1 = createRepositoryWithEventForTest(
                eventType = ModelEvent.EventType.STREAM_RATE_CRITICAL_EVENT,
                timeStart = timeStart.minusMillis(4000L),
                angleStart = 66.0,
                category = ModelEvent.Category.CRITICAL
            )

            val context1 = converterBeContextTest(
                timeStart = timeStart,
                meltInfo = defaultMeltInfoTest(),
                slagRate = ModelSlagRate(
                    slagRate = 0.16,
                    steelRate = 0.16
                ),
                frame = ModelFrame(
                    frameTime = timeStart
                )
            )
            val context2 = converterBeContextTest(
                meltInfo = meltInfoTest("211626-1606203452222" , "converter1"),
            )

            val context3 = converterBeContextTest(
                timeStart = timeStart.plusMillis(1000L),
                meltInfo = meltInfoTest("211626-1606203452222","converter1"),
                slagRate = ModelSlagRate(
                    slagRate = 0.0,
                    steelRate = 0.0
                ),
                frame = ModelFrame(
                    frameTime = timeStart.plusMillis(1000L)
                )
            )
            val converterFacade1 = converterFacadeTest(
                roundingWeight = 0.5,
                streamRateWarningPoint = 0.1,
                streamRateCriticalPoint = 0.15,
                currentState = createCurrentStateForTest(
                    lastAngle = 66.0,
                    lastSteelRate = 0.16,
                    avgStreamRate = 0.16
                ),
                converterRepository = repository1
            )

            converterFacade1.handleMath(context1)
            converterFacade1.handleMeltInfo(context2)
            converterFacade1.handleMath(context3)

            assertEquals(ModelSignaler.ModelSignalerLevel.CRITICAL, context1.signaler.level)
            assertEquals(ModelSignaler.ModelSignalerLevel.NO_SIGNAL, context3.signaler.level)
        }
    }
}
