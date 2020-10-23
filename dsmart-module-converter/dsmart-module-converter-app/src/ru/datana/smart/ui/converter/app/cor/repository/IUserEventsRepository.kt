package ru.datana.smart.ui.converter.app.cor.repository

interface IUserEventsRepository<T> {
    fun put(event: T)
    fun getActive(): List<T>
    fun getAll(): List<T>
}
