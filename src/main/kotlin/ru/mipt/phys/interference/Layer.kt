package ru.mipt.phys.interference

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import scientifik.kmath.geometry.Euclidean2DSpace
import scientifik.kmath.geometry.Vector2D
import scientifik.kmath.geometry.Vector3D
import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.toComplex
import scientifik.kmath.prob.RandomGenerator
import scientifik.kmath.prob.Sampler
import scientifik.kmath.prob.SamplerSpace

interface Layer : Emitter {
    val source: Emitter
}

typealias Transparency = (Vector2D) -> Complex

class Transparant(
    val generator: RandomGenerator,
    val scope: CoroutineScope,
    override val source: Emitter,
    val distribution: Sampler<Vector2D>,
    val position: Vector3D,
    val transparency: Transparency = { 1.0.toComplex() }
) : Layer {

    override suspend fun emit(): Flow<Wave> {
        return distribution.sample(generator).map { point ->
            val originPoint = Vector3D(
                position.x + point.x,
                position.y + point.y,
                position.z
            )

            val interference = Interference(scope, position, source)
            val amplitude = interference*transparency(point)
            amplitude.wave(position)
        }
    }

    companion object {
        private val poinSamplerSpace = SamplerSpace(Euclidean2DSpace)

        fun slit(
            generator: RandomGenerator,
            scope: CoroutineScope,
            source: Emitter,
            position: Vector3D,
            width: Double,
            height: Double = 10.0, // 1cm
            center: Vector2D = Euclidean2DSpace.zero
        ): Layer = Transparant(
            generator,
            scope,
            source,
            RectangularDistribution(width, height, center),
            position
        )

//        fun doubleSlit(
//            generator: RandomGenerator,
//            source: Emitter,
//            position: Vector3D,
//            distance: Double,
//            width: Double,
//            height: Double = 10.0, // 1cm
//            center: Vector2D = Euclidean2DSpace.zero
//        ): Layer {
//            val centerLeft = center - Vector2D(distance / 2, 0.0)
//            val centerRight = center + Vector2D(distance / 2, 0.0)
//
//            val distribution = poinSamplerSpace.run {
//                RectangularDistribution(width, height, centerLeft) +
//                        RectangularDistribution(width, height, centerRight)
//            }
//
//            return Transparant(
//                generator,
//                source,
//                distribution,
//                position
//            )
//        }
    }
}