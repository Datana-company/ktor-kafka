package ru.datana.smart.ui.converter.common.repositories

import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant

interface ICurrentStateRepository {
    suspend fun get(id: String): CurrentState?
    suspend fun create(currentState: CurrentState): CurrentState?
    suspend fun update(currentState: CurrentState): CurrentState?
    suspend fun delete(id: String): CurrentState?
    suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): CurrentState?
    suspend fun updateAngles(id: String, lastAngles: ModelAngles): CurrentState?
    suspend fun updateSlagRate(id: String, lastSlagRate: ModelSlagRate): CurrentState?
    suspend fun updateStreamRate(id: String, avgStreamRate: ModelSlagRate): CurrentState?
    suspend fun updateLastTimeAngles(id: String, lastTimeAngles: Instant): CurrentState?
    suspend fun updateLastTimeFrame(id: String, lastTimeFrame: Instant): CurrentState?


    companion object {
        val NONE = object : ICurrentStateRepository{
            override suspend fun get(id: String): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun create(currentState: CurrentState): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun update(currentState: CurrentState): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun delete(id: String): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun updateAngles(id: String, lastAngles: ModelAngles): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun updateSlagRate(id: String, lastSlagRate: ModelSlagRate): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun updateStreamRate(id: String, avgStreamRate: ModelSlagRate): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun updateLastTimeAngles(id: String, lastTimeAngles: Instant): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun updateLastTimeFrame(id: String, lastTimeFrame: Instant): CurrentState? {
                TODO("Not yet implemented")
            }

        }
    }
}
