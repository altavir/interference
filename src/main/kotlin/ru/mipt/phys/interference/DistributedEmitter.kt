package ru.mipt.phys.interference

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import scientifik.kmath.geometry.Vector2D
import scientifik.kmath.geometry.Vector3D
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler

/**
 * Generate a light source with given intensity distribution and given spectrum.
 * If [direction] is null, generates spherical waves, otherwise generates planar
 */
class DistributedEmitter(
    val generator: RandomGenerator,
    val distribution: Sampler<Vector2D>,
    val amplitude: Amplitude,
    val position: Vector3D,
    val direction: Vector3D? = null
) : Emitter {

    private fun generateWave(point: Vector2D): Wave {
        //TODO take rotation into account
        val originPoint = Vector3D(
            position.x + point.x,
            position.y + point.y,
            position.z
        )

        return amplitude.wave(originPoint, direction)
    }

//    override val lambda: Double get() = amplitude.lambda

    override suspend fun emit(): Flow<Wave> = distribution.sample(generator).map { generateWave(it) }

    companion object {
        fun circle(
            generator: RandomGenerator,
            amplitude: Amplitude,
            radius: Double,
            position: Vector3D = Vector3D(0.0, 0.0, 0.0),
            direction: Vector3D? = null
        ) = DistributedEmitter(
            generator,
            CircleDistribution(radius),
            amplitude,
            position,
            direction
        )
    }
}