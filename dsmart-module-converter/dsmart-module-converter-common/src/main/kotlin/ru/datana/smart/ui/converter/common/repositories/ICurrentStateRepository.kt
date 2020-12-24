package ru.datana.smart.ui.converter.common.repositories

import ru.datana.smart.ui.converter.common.models.CurrentState

interface ICurrentStateRepository {
    suspend fun get(id: String): CurrentState?
    suspend fun create(currentState: CurrentState): CurrentState?
    suspend fun update(currentState: CurrentState): CurrentState?

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

        }
    }
}
