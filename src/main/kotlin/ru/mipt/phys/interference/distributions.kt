package ru.mipt.phys.interference

import scientifik.kmath.chains.Chain
import scientifik.kmath.chains.SimpleChain
import scientifik.kmath.chains.filter
import scientifik.kmath.geometry.Euclidean2DSpace
import scientifik.kmath.geometry.Vector2D
import scientifik.kmath.geometry.r
import scientifik.kmath.prob.Distribution
import scientifik.kmath.prob.RandomGenerator
import kotlin.math.abs

class CircleDistribution(val r: Double, val center: Vector2D = Euclidean2DSpace.zero) : Distribution<Vector2D> {
    override fun probability(arg: Vector2D): Double {
        val r = arg.r
        return if (r < this.r) {
            2.0 / r // 2 PI r / PI r^2
        } else {
            0.0
        }
    }

    override fun sample(generator: RandomGenerator): Chain<Vector2D> {
        return SimpleChain {
            val x = (generator.nextDouble() - 0.5) * 2 * r + center.x
            val y = (generator.nextDouble() - 0.5) * 2 * r + center.y
            Vector2D(x, y)
        }.filter { (it - center).r <= r }
    }

}

class RectangularDistribution(
    val dx: Double,
    val dy: Double,
    val center: Vector2D = Euclidean2DSpace.zero
) : Distribution<Vector2D> {
    init {
        require(dx > 0)
        require(dy > 0)
    }

    override fun probability(arg: Vector2D): Double {
        val r = arg - center
        return if (abs(r.x) >= dx || abs(r.y) >= dy) {
            0.0
        } else {
            1.0 / 4 / dx / dy
        }
    }

    override fun sample(generator: RandomGenerator): Chain<Vector2D> {
        return SimpleChain {
            val x = center.x + (generator.nextDouble() - 0.5) * 2 * dx
            val y = center.y + (generator.nextDouble() - 0.5) * 2 * dy
            Vector2D(x, y)
        }
    }
}