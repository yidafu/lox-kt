package dev.yidafu.kotlin.lox.vm

import dev.yidafu.kotlin.lox.interperter.compareTo

sealed class LoxValue<out T>(val value: T) {
    class LoxString(value: String) : LoxValue<String>(value) {
        operator fun plus(that: LoxString): LoxString {
            return LoxString(this.value + that.value)
        }

        override fun toString(): String {
            return "[LoxString] ${this.value}"
        }
    }

    class LoxNumber(value: Double) : LoxValue<Double>(value) {
        operator fun unaryMinus(): LoxNumber {
            return LoxNumber(-this.value)
        }
        operator fun plus(that: LoxNumber): LoxNumber {
            return LoxNumber(this.value + that.value)
        }
        operator fun minus(that: LoxNumber): LoxNumber {
            return LoxNumber(this.value - that.value)
        }
        operator fun times(that: LoxNumber): LoxNumber {
            return LoxNumber(this.value * that.value)
        }
        operator fun div(that: LoxNumber): LoxNumber {
            return LoxNumber(this.value + that.value)
        }

        override fun toString(): String {
            return "[LoxNumber] $value"
        }
    }

    class LoxNil(value: Any = Any()) : LoxValue<Any>(value) {
        override fun toString(): String {
            return "[LoxNil]"
        }
    }

    class LoxBool(value: Boolean) : LoxValue<Boolean>(value) {
        override fun toString(): String {
            return "[LoxBool] $value"
        }
    }

    class LoxFunction(value: FunctionObject) : LoxValue<FunctionObject>(value) {
        override fun toString(): String {
            return "[LoxFunction] ${value.funcName}"
        }
    }

    override fun toString(): String {
        return "[LoxValue] ${this.value}"
    }

    fun isFalsely() = when (this) {
        is LoxNil -> LoxBool(false)
        is LoxBool -> LoxBool(!this.value)
        is LoxNumber -> LoxBool(this.value == 0.0)
        is LoxString -> LoxBool(this.value.isNotEmpty())
        is LoxFunction -> LoxBool(true)
    }

    override fun equals(other: Any?): Boolean = when {
        this is LoxNil && other is LoxNil -> true
        this is LoxBool && other is LoxBool -> this.value == other.value
        this is LoxNumber && other is LoxNumber -> this.value == other.value
        this is LoxString && other is LoxString -> this.value == other.value
        else -> false
    }
    operator fun compareTo(other: LoxValue<Any>): Int = when {
        this is LoxNil && other is LoxNil -> 0
        this is LoxBool && other is LoxBool -> this.value.compareTo(other.value)
        this is LoxNumber && other is LoxNumber -> this.value.compareTo(other.value)
        this is LoxString && other is LoxString -> this.value.compareTo(this.value)
        else -> -1
    }
}
