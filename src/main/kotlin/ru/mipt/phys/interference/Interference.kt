package ru.mipt.phys.interference

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import scientifik.kmath.geometry.Vector3D
import scientifik.kmath.operations.Complex

internal class PrecisionBox<T, R>(initial: R, val reducer: suspend (R, T) -> R)  {
    val channel = Channel<T>(Channel.RENDEZVOUS)

    val precision = atomic(0)
    val value = atomic(initial)

    suspend fun send(value: T) {
        channel.send(value)
    }

    suspend fun next(): Pair<Int, R> {
        val nextFromChannel = channel.receive()
        val value = value.updateAndGet { prev ->
            reducer(prev, nextFromChannel)
        }
        val precision = precision.incrementAndGet()
        return precision to value
    }

    suspend fun value(precision: Int): R {
        while (this.precision.value < precision) {
            next()
        }
        return value.value
    }
}

class Interference(
    scope: CoroutineScope,
    val position: Vector3D,
    val source: Emitter
) : Amplitude {

    private val sum = PrecisionBox<Wave, Amplitude>(AmplitudeSpace.zero) { reducer, wave ->
        reducer + wave.amplitudeAt(position)
    }

    val job = scope.launch {
        source.emit().collect {
            //ensure that emit is symmetrical for all sources
            sum.send(it)
        }
    }

    override suspend fun value(precision: Int, lambda: Double): Complex = sum
        .value(precision)//sum of amplitudes
        .value(precision, lambda)//computation of amplitude value
}

fun Amplitude.wave(position: Vector3D, direction: Vector3D? = null): Wave = if (direction != null) {
    PlaneWave(position, direction, this)
} else {
    SphericalWave(position, this)
}

fun Emitter.interfereAt(scope: CoroutineScope, point: Vector3D): Interference =
    Interference(scope, point, this)