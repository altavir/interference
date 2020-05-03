package ru.mipt.phys.interference

import kotlinx.atomicfu.atomic
import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
import scientifik.kmath.operations.Space

const val DEFAULT_LAMBDA = 0.0

interface Amplitude {
    suspend fun value(precision: Int, lambda: Double): Complex

    companion object {

    }
}

operator fun Amplitude.plus(other: Amplitude): Amplitude = AmplitudeSpace.add(this, other)

operator fun Amplitude.times(other: Complex): Amplitude = CachedAmplitude { precision, lambda ->
    value(precision, lambda) * other
}

operator fun Amplitude.div(number: Number): Amplitude = AmplitudeSpace.multiply(this, 1.0 / number.toDouble())

fun Complex.rotatePhase(phase: Double): Complex = ComplexField.run { this@rotatePhase * exp(ComplexField.i * phase) }

class CachedAmplitude(
    val compute: suspend (precision: Int, lambda: Double) -> Complex
) : Amplitude {
    private var cachedPrecision = atomic(0)

    private var cachedValue = atomic<Complex>(ComplexField.zero)

    override suspend fun value(precision: Int, lambda: Double): Complex {
        require(lambda == DEFAULT_LAMBDA){"Non-default lambdas are not supported"}
        return if (cachedPrecision.value > precision) {
            cachedValue.value
        } else {
            val value = compute(precision, lambda)
            cachedValue.lazySet(value)
            cachedPrecision.lazySet(precision)
            value
        }
    }
}

object AmplitudeSpace : Space<Amplitude> {

    override val zero: Amplitude = object : Amplitude {
        override suspend fun value(precision: Int, lambda: Double): Complex = ComplexField.zero
    }

    override fun add(a: Amplitude, b: Amplitude): Amplitude {
        return CachedAmplitude { precision, lambda ->
            a.value(precision, lambda) + b.value(precision, lambda)
        }
    }

    override fun multiply(a: Amplitude, k: Number): Amplitude {
        return CachedAmplitude { precision, lambda ->
            a.value(precision, lambda) * k
        }
    }
}