package ru.mipt.phys.interference

import scientifik.kmath.geometry.Euclidean3DSpace
import scientifik.kmath.geometry.Vector3D
import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.invoke
import kotlin.math.PI

sealed class Wave {
    /**
     * Get or compute electric field complex amplitude vector at given [position]
     */
    abstract fun amplitudeAt(position: Vector3D): Amplitude
}

class SphericalWave(
    val origin: Vector3D,
    val amplitude: Amplitude
) : Wave() {
    override fun amplitudeAt(position: Vector3D): Amplitude = Euclidean3DSpace {
        val distance: Double = (position - origin).norm()
        CachedAmplitude { precision, lambda ->
            val phase = distance * 2 * PI / lambda
            val originValue: Complex = this@SphericalWave.amplitude.value(precision, lambda)
            (originValue / distance).rotatePhase(phase)
        }
    }
}

class PlaneWave(
    val origin: Vector3D,
    val direction: Vector3D,
    val amplitude: Amplitude
) : Wave() {
    override fun amplitudeAt(position: Vector3D): Amplitude = Euclidean3DSpace {
        val r = position - origin
        CachedAmplitude { precision, lambda ->
            val phase = (r dot direction) * 2 * PI / lambda
            val originValue: Complex = this@PlaneWave.amplitude.value(precision, lambda)
            originValue.rotatePhase(phase)
        }
    }
}

//TODO add beam