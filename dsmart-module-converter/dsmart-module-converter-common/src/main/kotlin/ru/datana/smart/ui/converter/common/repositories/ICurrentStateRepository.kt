package ru.datana.smart.ui.converter.common.repositories

import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant

interface ICurrentStateRepository {
    suspend fun get(id: String): CurrentState
    suspend fun getAllSlagRates(id: String): MutableList<ModelSlagRate>
    suspend fun currentMeltInfo(id: String): ModelMeltInfo
    suspend fun currentMeltId(id: String): String
    suspend fun currentAngle(id: String): Double
    suspend fun avgStreamRate(id: String): Double //под вопросом
    suspend fun create(currentState: CurrentState): CurrentState
    suspend fun update(currentState: CurrentState): CurrentState
    suspend fun delete(id: String): CurrentState
    suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): ModelMeltInfo
    suspend fun updateAngles(id: String, lastAngles: ModelAngles): ModelAngles
    suspend fun updateStreamRate(id: String, avgStreamRate: Double): Double
    suspend fun addSlagRate(id: String, slagRate: ModelSlagRate): CurrentState
    suspend fun compareAndUpdateLastTimeAngles(id: String, lastTimeAngles: Instant): Instant
    suspend fun compareAndUpdateLastTimeFrame(id: String, lastTimeFrame: Instant): Instant


    companion object {
        val NONE = object : ICurrentStateRepository{

            override suspend fun get(id: String): CurrentState {
                TODO("Not yet implemented")
            }

            override suspend fun getAllSlagRates(id: String): MutableList<ModelSlagRate> {
                TODO("Not yet implemented")
            }

            override suspend fun currentMeltInfo(id: String): ModelMeltInfo {
                TODO("Not yet implemented")
            }

            override suspend fun currentMeltId(id: String): String {
                TODO("Not yet implemented")
            }

            override suspend fun currentAngle(id: String): Double {
                TODO("Not yet implemented")
            }

            override suspend fun avgStreamRate(id: String): Double {
                TODO("Not yet implemented")
            }

            override suspend fun create(currentState: CurrentState): CurrentState {
                TODO("Not yet implemented")
            }

            override suspend fun update(currentState: CurrentState): CurrentState {
                TODO("Not yet implemented")
            }

            override suspend fun delete(id: String): CurrentState {
                TODO("Not yet implemented")
            }

            override suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): ModelMeltInfo {
                TODO("Not yet implemented")
            }

            override suspend fun updateAngles(id: String, lastAngles: ModelAngles): ModelAngles {
                TODO("Not yet implemented")
            }

            override suspend fun updateStreamRate(id: String, avgStreamRate: Double): Double {
                TODO("Not yet implemented")
            }

            override suspend fun addSlagRate(id: String, slagRate: ModelSlagRate): CurrentState {
                TODO("Not yet implemented")
            }

            override suspend fun compareAndUpdateLastTimeAngles(id: String, lastTimeAngles: Instant): Instant {
                TODO("Not yet implemented")
            }

            override suspend fun compareAndUpdateLastTimeFrame(id: String, lastTimeFrame: Instant): Instant {
                TODO("Not yet implemented")
            }

        }
    }
}
