package ru.mipt.phys.interference

import kotlinx.coroutines.flow.Flow

interface Emitter {
    /**
     * Provide a list of waves of given [size] distributed according to the source rules.
     */
    suspend fun emit(): Flow<Wave>
}

