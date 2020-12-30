package ru.datana.smart.ui.converter.common.repositories

import ru.datana.smart.ui.converter.common.models.*
import java.time.Instant

interface ICurrentStateRepository {
    suspend fun get(): CurrentState?
    suspend fun getAllSlagRates(): MutableList<ModelSlagRate>?
    suspend fun currentMeltInfo(): ModelMeltInfo?
    suspend fun currentMeltId(): String
    suspend fun currentAngle(): Double
    suspend fun lastAvgSteelRate(): Double
    suspend fun lastAvgSlagRate(): Double
    suspend fun lastTimeAngles(): Instant
    suspend fun lastTimeFrame(): Instant
    suspend fun create(currentState: CurrentState): CurrentState
    suspend fun update(currentState: CurrentState): CurrentState
    suspend fun delete(): CurrentState
    suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): ModelMeltInfo?
    suspend fun updateAngles(lastAngles: ModelAngles): ModelAngles?
    suspend fun addSlagRate(slagRate: ModelSlagRate): CurrentState?
    suspend fun updateLastAvgSteelRate(avgSteelRate: Double): Double?
    suspend fun updateLastAvgSlagRate(avgSlagRate: Double): Double?
    suspend fun updateLastTimeAngles(lastTimeAngles: Instant): Instant?
    suspend fun updateLastTimeFrame(lastTimeFrame: Instant): Instant?


    companion object {
        val NONE = object : ICurrentStateRepository{

            override suspend fun get(): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun getAllSlagRates(): MutableList<ModelSlagRate>? {
                TODO("Not yet implemented")
            }

            override suspend fun currentMeltInfo(): ModelMeltInfo? {
                TODO("Not yet implemented")
            }

            override suspend fun currentMeltId(): String {
                TODO("Not yet implemented")
            }

            override suspend fun currentAngle(): Double {
                TODO("Not yet implemented")
            }

            override suspend fun lastAvgSteelRate(): Double {
                TODO("Not yet implemented")
            }

            override suspend fun lastAvgSlagRate(): Double {
                TODO("Not yet implemented")
            }

            override suspend fun lastTimeAngles(): Instant {
                TODO("Not yet implemented")
            }

            override suspend fun lastTimeFrame(): Instant {
                TODO("Not yet implemented")
            }

            override suspend fun create(currentState: CurrentState): CurrentState {
                TODO("Not yet implemented")
            }

            override suspend fun update(currentState: CurrentState): CurrentState {
                TODO("Not yet implemented")
            }

            override suspend fun delete(): CurrentState {
                TODO("Not yet implemented")
            }

            override suspend fun updateMeltInfo(meltInfo: ModelMeltInfo): ModelMeltInfo? {
                TODO("Not yet implemented")
            }

            override suspend fun updateAngles(lastAngles: ModelAngles): ModelAngles? {
                TODO("Not yet implemented")
            }

            override suspend fun updateLastAvgSteelRate(avgSlagRate: Double): Double? {
                TODO("Not yet implemented")
            }

            override suspend fun updateLastAvgSlagRate(avgSlagRate: Double): Double? {
                TODO("Not yet implemented")
            }

            override suspend fun addSlagRate(slagRate: ModelSlagRate): CurrentState? {
                TODO("Not yet implemented")
            }

            override suspend fun updateLastTimeAngles(lastTimeAngles: Instant): Instant? {
                TODO("Not yet implemented")
            }

            override suspend fun updateLastTimeFrame(lastTimeFrame: Instant): Instant? {
                TODO("Not yet implemented")
            }

        }
    }
}
