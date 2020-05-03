package ru.mipt.phys.interference

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import scientifik.kmath.geometry.Vector2D
import scientifik.kmath.geometry.Vector3D
import scientifik.kmath.histogram.RealHistogram
import scientifik.kmath.operations.r
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import kotlin.math.pow

suspend fun Emitter.project(
    generator: RandomGenerator,
    distribution: Sampler<Vector2D>,
    position: Vector3D,
    precision: Int,
    lambda: Double,
    vararg ranges: Pair<ClosedFloatingPointRange<Double>, Int>
): RealHistogram {
    val histogram = RealHistogram.fromRanges(*ranges)

    distribution.sample(generator).take(precision).collect { point ->
        val target = Vector3D(
            position.x + point.x,
            position.y + point.y,
            position.z
        )
        emit().take(precision).collect { wave ->
            val value = wave.amplitudeAt(target).value(precision, lambda)
            histogram.putWithWeight(point, value.r.pow(2))
        }

    }
    return histogram
}